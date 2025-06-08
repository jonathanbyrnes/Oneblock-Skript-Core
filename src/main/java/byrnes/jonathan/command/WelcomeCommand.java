package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.listener.CoreListener;
import byrnes.jonathan.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Command("welcome")
public class WelcomeCommand {

    private final ConfigHelper config;
    private final CoreListener core;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public WelcomeCommand(ConfigHelper config, CoreListener core) {
        this.config = config;
        this.core = core;
    }

    @Command("")
    @Permission("oneblockutils.welcome.use")
    public void onWelcome(CommandContext<CommandSender> context) {
        if (!(context.sender() instanceof Player player)) return;

        long now = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - lastUsed < 15_000) {
            player.sendMessage(config.getMessage("core.welcome.cooldown-message"));
            return;
        }

        if (!core.isWelcomeActive()) {
            player.sendMessage(config.getMessage("core.welcome.no-welcome-message"));
            return;
        }

        UUID targetUUID = core.getNewPlayer();
        if (targetUUID == null) return;

        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null) return;

        // Fetch the broadcast template from config
        String template = config.config().getString(
                "core.welcome.broadcast-message",
                "%chattags%%vault_prefix%{player_name}&f: Welcome {target_name} to Oneblock!"
        );

        // Replace the {player_name} placeholder
        template = template.replace("{player_name}", player.getName());
        template = template.replace("{target_name}", target.getName());
        String tagStr = PlaceholderAPI.setPlaceholders(player, "%chattags%");
        String prefixStr = PlaceholderAPI.setPlaceholders(player, "%vault_prefix%");

        template = template.replace("%chattags%", tagStr);
        template = template.replace("%vault_prefix%", prefixStr);

        // Send the broadcast
        Component broadcast = MessageUtil.legacyColor(template);
        Bukkit.broadcast(broadcast);

        String rewardCommand = config.config().getString("core.welcome.reward-command")
                .replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);

        player.sendMessage(config.getMessage("core.welcome.reward-message"));

        cooldowns.put(player.getUniqueId(), now);
    }
}
