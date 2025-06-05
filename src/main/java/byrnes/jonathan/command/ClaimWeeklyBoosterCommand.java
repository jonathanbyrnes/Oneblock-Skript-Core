package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("claimweeklybooster")
public class ClaimWeeklyBoosterCommand {

    private final ConfigHelper config;

    public ClaimWeeklyBoosterCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("<player>")
    @Permission("oneblockutils.claimweeklybooster.use")
    public void claimBooster(
            CommandContext<CommandSender> context,
            @Argument("player") final Player player
    ) {
        CommandSender sender = context.sender();

        if (hasCooldown(player)) {
            player.sendMessage(config.getMessage("booster-claim.messages.already-claimed"));
            return;
        }

        BoosterGroup group = resolveBoosterGroup(player);
        if (group == null) {
            sender.sendMessage(Component.text("Player has no matching booster group configured."));
            return;
        }

        applyCooldown(player);
        giveBooster(player, group);
        player.sendMessage(config.getMessage("booster-claim.messages.booster-claimed"));

    }

    @Command("reload")
    @Permission("oneblockutils.claimweeklybooster.reload")
    public void reloadCommand(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("booster-claim.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.claimweeklybooster.help")
    public void helpCommand(CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("booster-claim.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    private boolean hasCooldown(Player player) {
        String cooldownNode = config.config().getString("booster-claim.cooldown-node", "boosterreward.cooldown");
        return player.hasPermission(cooldownNode);
    }

    private BoosterGroup resolveBoosterGroup(Player player) {
        ConfigurationSection groups = config.config().getConfigurationSection("booster-claim.groups");
        if (groups == null) return null;

        for (String key : groups.getKeys(false)) {
            ConfigurationSection section = groups.getConfigurationSection(key);
            if (section == null) continue;

            String permission = section.getString("permission");
            if (permission == null || !player.hasPermission(permission)) continue;

            String boosterId = section.getString("booster");
            int amount = section.getInt("amount", 1);
            return new BoosterGroup(boosterId, amount);
        }
        return null;
    }

    private void applyCooldown(Player player) {
        String cooldownNode = config.config().getString("booster-claim.cooldown-node", "boosterreward.cooldown");
        String duration = config.config().getString("booster-claim.cooldown-duration", "7d");
        String server = config.config().getString("booster-claim.server", "oneblock");

        String cmd = String.format(
                "lp user %s permission settemp %s true %s server=%s",
                player.getName(), cooldownNode, duration, server
        );

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    private void giveBooster(Player player, BoosterGroup group) {
        String cmd = String.format("ei give %s %s %d", player.getName(), group.boosterId(), group.amount());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    private record BoosterGroup(String boosterId, int amount) {
    }

}
