package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
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
