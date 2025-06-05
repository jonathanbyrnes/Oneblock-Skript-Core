package gg.tsmc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("staff")
@Permission("group.helper")
public class StaffCommand {

    @Command("<action> [player] [reason]")
    public void onStaffCommand(
            final CommandContext<CommandSender> context,
            @Argument(value = "action", suggestions = "staff-actions") final String action,
            @Argument("player") final Player target,              // now nullable
            @Argument("reason") final String reason               // now nullable
    ) {
        CommandSender sender = context.sender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        switch (action.toLowerCase()) {
            case "spy" -> player.performCommand("kcs tcs");
            case "filter" -> player.performCommand("kcs tvn");
            case "anticheat" -> player.performCommand("vulcan:alerts");
            case "clearchat" -> player.performCommand("chatsentry:clearchat");
            case "togglechat" -> player.performCommand("chatsentry:togglechat");

            case "ban" -> {
                if (target != null) {
                    player.performCommand("fvban " + target.getName());
                } else {
                    player.sendMessage("§cUsage: /staff ban <player>");
                }
            }

            case "mute" -> {
                if (target != null) {
                    player.performCommand("fvmute " + target.getName());
                } else {
                    player.sendMessage("§cUsage: /staff mute <player>");
                }
            }

            case "warn" -> {
                if (target != null && reason != null) {
                    player.performCommand("warn " + target.getName() + " " + reason);
                } else {
                    player.sendMessage("§cUsage: /staff warn <player> <reason>");
                }
            }

            default -> player.sendMessage("§cUnknown staff action.");
        }
    }

    @Suggestions("staff-actions")
    public List<String> suggestStaffActions(CommandContext<CommandSender> context, String input) {
        return List.of(
                        "spy", "filter", "anticheat", "clearchat",
                        "togglechat", "ban", "mute", "warn"
                ).stream()
                .filter(s -> input.isEmpty() || s.startsWith(input.toLowerCase()))
                .toList();
    }
}
