package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

@Command("seasonalkey")
public class SeasonalKeyCommand {

    private final ConfigHelper config;

    public SeasonalKeyCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("give <player>")
    @Permission("oneblockutils.seasonalkey.give")
    public void giveKey(
            CommandContext<CommandSender> context,
            @Argument("player") final Player target
    ) {
        String requiredGroup = config.config().getString("seasonal.required-group", "group.fruit");
        String keyType = config.config().getString("seasonal.key-name", "sakura");
        String claimPermission = config.config().getString("seasonal.claim-permission", "seasonal.key");

        if (!target.hasPermission(requiredGroup)) {
            target.sendMessage(config.getMessage("seasonal.messages.not-eligible"));
            return;
        }

        if (target.hasPermission(claimPermission)) {
            target.sendMessage(config.getMessage("seasonal.messages.already-claimed"));
            return;
        }

        // Give permission + key
        String grantPermissionCmd = String.format("lp user %s permission set %s", target.getName(), claimPermission);
        String giveKeyCmd = String.format("crate key give %s %s 1", target.getName(), keyType);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), grantPermissionCmd);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("OneblockUtils"), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveKeyCmd), 1L);

        target.sendMessage(config.getMessage("seasonal.messages.key-given"));
    }

    @Command("clear")
    @Permission("oneblockutils.seasonalkey.clear")
    public void clearKeys(CommandContext<CommandSender> context) {
        String claimPermission = config.config().getString("seasonal.claim-permission", "seasonal.key");
        String command = String.format("lp bulkupdate all delete \"\"permission == %s\"\"", claimPermission);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        context.sender().sendMessage(Component.text("✅ Bulk update dispatched to clear all claimed permissions."));
    }

    @Command("reload")
    @Permission("oneblockutils.seasonalkey.reload")
    public void reloadCommand(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("seasonal.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.seasonalkey.help")
    public void helpCommand(CommandContext<CommandSender> context) {
        for (String line : config.getList("seasonal.messages.help")) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }
}
