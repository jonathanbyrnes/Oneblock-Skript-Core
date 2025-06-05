package byrnes.jonathan.listener;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.manager.DropLockManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropLockListener implements Listener {

    private final DropLockManager lockManager;
    private final ConfigHelper config;

    public DropLockListener(DropLockManager lockManager, ConfigHelper config) {
        this.lockManager = lockManager;
        this.config = config;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isLocked(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(config.getMessage("core.droplock.locked"));
        }
    }

}
