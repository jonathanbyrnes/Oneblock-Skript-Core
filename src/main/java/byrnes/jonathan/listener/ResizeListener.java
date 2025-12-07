package byrnes.jonathan.listener;

import byrnes.jonathan.config.ConfigHelper;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResizeListener implements Listener {

    private static final double DEFAULT_PLAYER_SCALE = 1.0D;

    private final ConfigHelper config;
    private final Set<String> blockedWorlds = new HashSet<>();
    private boolean blockingEnabled;

    public ResizeListener(ConfigHelper config) {
        this.config = config;
        reload();
    }

    public void reload() {
        this.blockingEnabled = config.config().getBoolean("resize.blocking.enabled", false);

        this.blockedWorlds.clear();

        List<String> worlds = config.getList("resize.blocking.worlds");
        if (worlds != null) {
            for (String worldName : worlds) {
                if (worldName != null && !worldName.isEmpty()) {
                    this.blockedWorlds.add(worldName.toLowerCase());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        enforceDefaultScaleIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        enforceDefaultScaleIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        enforceDefaultScaleIfNeeded(event.getPlayer());
    }

    private void enforceDefaultScaleIfNeeded(Player player) {
        if (!blockingEnabled) {
            return;
        }

        World world = player.getWorld();
        if (world == null) {
            return;
        }

        if (!isBlockedWorld(world)) {
            return;
        }

        AttributeInstance scaleAttr = player.getAttribute(Attribute.SCALE);
        if (scaleAttr == null) {
            return;
        }

        if (scaleAttr.getBaseValue() != DEFAULT_PLAYER_SCALE) {
            scaleAttr.setBaseValue(DEFAULT_PLAYER_SCALE);
        }
    }

    private boolean isBlockedWorld(World world) {
        return blockedWorlds.contains(world.getName().toLowerCase());
    }
}
