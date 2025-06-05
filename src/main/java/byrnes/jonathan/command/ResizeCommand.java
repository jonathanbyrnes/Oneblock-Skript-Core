package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("resize")
public class ResizeCommand {

    private final ConfigHelper config;

    public ResizeCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("<scale> <player>")
    @Permission("oneblockutils.resize.use")
    public void onResize(
            CommandContext<CommandSender> context,
            @Argument("scale") final String scaleArg,
            @Argument("player") final Player target
    ) {
        CommandSender sender = context.sender();

        // Ensure scale option exists in config
        if (!config.config().isConfigurationSection("resize.options." + scaleArg)) {
            sender.sendMessage(config.getMessage("resize.messages.invalid-scale", "{scale}", scaleArg));
            return;
        }

        // Check permission requirement
        String requiredPermission = config.config().getString("resize.options." + scaleArg + ".permission", "none");
        if (!"none".equalsIgnoreCase(requiredPermission) && !target.hasPermission(requiredPermission)) {
            sender.sendMessage(config.getMessage("resize.messages.no-permission",
                    "{player}", target.getName(),
                    "{scale}", scaleArg));
            return;
        }

        // Attempt to parse and set scale
        try {
            double scale = Double.parseDouble(scaleArg);
            AttributeInstance scaleAttr = target.getAttribute(Attribute.SCALE);
            if (scaleAttr == null) {
                sender.sendMessage(Component.text("Could not access SCALE attribute."));
                return;
            }

            scaleAttr.setBaseValue(scale);
            sender.sendMessage(config.getMessage("resize.messages.success",
                    "{player}", target.getName(),
                    "{scale}", scaleArg));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid scale number: " + scaleArg));
        }
    }

    @Command("reload")
    @Permission("oneblockutils.resize.reload")
    public void reloadCommand(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("resize.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.resize.help")
    public void helpCommand(CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("resize.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }
}
