package gg.tsmc.command;

import gg.tsmc.config.ConfigHelper;
import gg.tsmc.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.*;

@Command("claimweeklyreward")
public class ClaimWeeklyRewardCommand {

    private final ConfigHelper config;
    private final Random random = new Random();

    public ClaimWeeklyRewardCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("<player>")
    @Permission("oneblockutils.weeklyreward.use")
    public void onClaim(
            CommandContext<CommandSender> context,
            @Argument("player") final Player target
    ) {
        CommandSender sender = context.sender();

        String cooldownPermission = config.config().getString("rewards.cooldown-permission", "weeklyreward.cooldown");
        String cooldownDuration = config.config().getString("rewards.cooldown-duration", "7d");
        String contextArgs = config.config().getString("rewards.luckperms-context", "");

        // Check cooldown
        if (target.hasPermission(cooldownPermission)) {
            target.sendMessage(config.getMessage("rewards.messages.already-claimed"));
            return;
        }

        ConfigurationSection groupSection = config.config().getConfigurationSection("rewards.groups");
        if (groupSection == null) {
            sender.sendMessage(Component.text("No reward groups configured."));
            return;
        }

        // Match first group the player qualifies for
        for (String group : groupSection.getKeys(false)) {
            String groupPermission = groupSection.getString(group + ".permission");
            if (groupPermission == null || !target.hasPermission(groupPermission)) continue;

            List<String> rewards = groupSection.getStringList(group + ".rewards");
            giveRewards(target, rewards);

            // Apply cooldown
            String setPerm = String.format("/lp user %s permission settemp %s true %s %s",
                    target.getName(), cooldownPermission, cooldownDuration, contextArgs);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setPerm);

            target.sendMessage(config.getMessage("rewards.messages.reward-claimed"));
            return;
        }

        sender.sendMessage(Component.text("No matching group found for player."));
    }

    @Command("reload")
    @Permission("oneblockutils.weeklyreward.reload")
    public void reloadCommand(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("rewards.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.weeklyreward.help")
    public void helpCommand(CommandContext<CommandSender> context) {
        for (String line : config.getList("rewards.messages.help")) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    private void giveRewards(Player player, List<String> rewards) {
        for (String entry : rewards) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2) continue;

            try {
                double chance = Double.parseDouble(parts[0]);
                if (random.nextDouble() * 100 <= chance) {
                    String cmd = parts[1].replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            } catch (NumberFormatException e) {
                // Invalid format — skip
            }
        }
    }
}
