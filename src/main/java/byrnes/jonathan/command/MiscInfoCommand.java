package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscInfoCommand {

    private final ConfigHelper config;
    private final Pattern LINK_PATTERN =
            Pattern.compile("^(.*)<link:(https?://[^>]+)>(.+?)</underlined>(.*)$");

    public MiscInfoCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("discord")
    @Permission("oneblockutils.discord")
    public void discord(CommandContext<CommandSender> context) {
        sendMessageList(context.sender(), "info.discord.message");
    }

    @Command("store")
    @Permission("oneblockutils.store")
    public void store(CommandContext<CommandSender> context) {
        sendMessageList(context.sender(), "info.store.message");
    }

    @Command("rules")
    @Permission("oneblockutils.rules")
    public void rules(CommandContext<CommandSender> context) {
        sendMessageList(context.sender(), "info.rules.message");
    }

    @Command("apply")
    @Permission("oneblockutils.apply")
    public void apply(CommandContext<CommandSender> context) {
        sendMessageList(context.sender(), "info.apply.message");
    }

    @Command("votes")
    @Permission("oneblockutils.votes")
    public void votes(CommandContext<CommandSender> context) {
        if (!(context.sender() instanceof Player player)) {
            return;
        }
        String votes = PlaceholderAPI.setPlaceholders(player, "%voteparty_totalvotes_alltime%");

        player.sendMessage((""));
        player.sendMessage(MessageUtil.legacyColor("&e&lYour Votes"));
        player.sendMessage((""));
        player.sendMessage(MessageUtil.color(
                "<#9485C6><bold>VOTES:<reset> <#BAA7F8>" + votes
        ));
        player.sendMessage((""));
        player.sendMessage(MessageUtil.color(
                "<#86FC3E>Type <bold>/vote</bold><reset><#86FC3E> to vote for us!"
        ));
        player.sendMessage((""));
    }

    @Command("pinata")
    @Permission("oneblockutils.pinata")
    public void pinata(CommandContext<CommandSender> context) {
        Player player = (Player) context.sender();
        player.performCommand("ewarp pinata");
    }

    @Command("fishinglake")
    @Permission("oneblockutils.fishinglake")
    public void fishingLake(CommandContext<CommandSender> context) {
        Player player = (Player) context.sender();
        player.performCommand("ewarp fishing");
    }

    @Command("boostclaim")
    @Permission("oneblockutils.boostclaim")
    public void boostClaim(CommandContext<CommandSender> context) {
        Player player = (Player) context.sender();
        player.performCommand("kit nitro");
    }

    @Command("nextreboot")
    @Permission("oneblockutils.nextreboot")
    public void nextReboot(CommandContext<CommandSender> context) {
        Player player = (Player) context.sender();
        player.performCommand("uar time");
    }

    @Command("warzone")
    @Permission("oneblockutils.warzone")
    public void warzone(CommandContext<CommandSender> context) {
        Player player = (Player) context.sender();
        player.performCommand("ewarp pvp");
    }

    @Command("info help")
    @Permission("oneblockutils.info.help")
    public void help(CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("info.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    @Command("reload")
    @Permission("oneblockutils.info.reload")
    public void reload(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("info.messages.reload-success"));
    }

    private void sendMessageList(CommandSender sender, String path) {
        List<String> lines = config.getList(path);

        for (String raw : lines) {
            Component message;
            Matcher m = LINK_PATTERN.matcher(raw);

            if (m.matches()) {
                Component before = MessageUtil.color(m.group(1));
                Component link = MessageUtil.color(m.group(3))
                        .clickEvent(ClickEvent.openUrl(m.group(2)));
                Component after = MessageUtil.color(m.group(4));
                message = before.append(link).append(after);
            } else {
                message = MessageUtil.color(raw);
            }

            sender.sendMessage(message);
        }
    }
}
