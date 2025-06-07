package byrnes.jonathan.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CreateIslandListener implements Listener {

    private final JavaPlugin plugin;

    public CreateIslandListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
                SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
                if (!superiorPlayer.hasIsland()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.performCommand("island create oneblock");
                    }, 45L);
                }
            }
        }
    }
}
