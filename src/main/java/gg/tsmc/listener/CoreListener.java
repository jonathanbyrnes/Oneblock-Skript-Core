package gg.tsmc.listener;

import gg.tsmc.config.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CoreListener implements Listener {

    private final ConfigHelper config;
    private final AtomicReference<UUID> newPlayer = new AtomicReference<>();
    private volatile boolean welcomeActive = false;

    public CoreListener(ConfigHelper config) {
        this.config = config;
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Only mark if it's the player's first time
        if (!player.hasPlayedBefore()) {
            newPlayer.set(player.getUniqueId());
            welcomeActive = true;

            int delay = config.config().getInt("core.welcome.active-seconds", 15);
            Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("OneblockUtils"),
                    () -> welcomeActive = false,
                    delay * 20L
            );
        }
    }

    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(true);
        }
    }

    public boolean isWelcomeActive() {
        return welcomeActive;
    }

    public UUID getNewPlayer() {
        return newPlayer.get();
    }
}
