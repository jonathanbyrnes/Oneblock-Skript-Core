package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.manager.DropLockManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

@Command("lock")
public class DropLockCommand {

    private final DropLockManager lockManager;
    private final ConfigHelper config;


    public DropLockCommand(DropLockManager lockManager, ConfigHelper config) {
        this.lockManager = lockManager;
        this.config = config;
    }

    @Command("")
    @Permission("oneblockutils.lock")
    public boolean onLock(CommandContext<CommandSender>context) {
        CommandSender sender = context.sender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        // Toggle lock
        lockManager.toggleLock(player.getUniqueId());

        if(lockManager.isLocked(player.getUniqueId())) {
            // Lock is being enabled
            player.sendMessage(config.getMessage("core.droplock.enabled"));
        } else {
            // Lock is being disabled
            player.sendMessage(config.getMessage("core.droplock.disabled"));
        }

        return true;
    }
}
