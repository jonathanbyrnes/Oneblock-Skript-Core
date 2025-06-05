package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.scheduler.BroadcastScheduler;
import byrnes.jonathan.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("broadcasts")
public class BroadcastsCommand {

    private final ConfigHelper config;
    private final BroadcastScheduler scheduler;

    public BroadcastsCommand(ConfigHelper config, BroadcastScheduler scheduler) {
        this.config = config;
        this.scheduler = scheduler;
    }

    @Command("reload")
    @Permission("oneblockutils.broadcast.reload")
    public void reloadCommand(final CommandContext<CommandSender> context) {
        scheduler.reload();
        context.sender().sendMessage(config.getMessage("broadcasts.messages-meta.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.broadcast.help")
    public void helpCommand(final CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("broadcasts.messages-meta.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }
}
