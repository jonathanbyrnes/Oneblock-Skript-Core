package byrnes.jonathan.listener;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.manager.GgWaveManager;
import byrnes.jonathan.util.MessageUtil;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GgWaveListener implements Listener {
    private static final Pattern GG_WORD = Pattern.compile("(?i)\\bgg\\b");
    private final GgWaveManager manager;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final ConfigHelper config;

    public GgWaveListener(GgWaveManager manager, ConfigHelper config) {
        this.manager = manager;
        this.config = config;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!manager.isActive()) return;

        Component original = event.message();
        String plain = PlainTextComponentSerializer.plainText().serialize(original);
        if (!GG_WORD.matcher(plain).find()) return;

        // Replace "gg" with a styled component
        Component replaced = original.replaceText(config -> {
            config.match(GG_WORD).replacement((match, in) -> manager.getRandomStyledComponent());
        });

        // Set the modified message (the "GG" replacement)
        event.message(replaced);

        // Apply a viewer-unaware renderer with the full formatted message
        event.renderer(ChatRenderer.viewerUnaware((source, displayName, message) -> {
            Player player = (Player) source;

            String tagStr    = PlaceholderAPI.setPlaceholders(player, "%chattags%");
            String prefixStr = PlaceholderAPI.setPlaceholders(player, "%vault_prefix%");
            String suffixStr = PlaceholderAPI.setPlaceholders(player, "%vault_suffix%");

            // Deserialize tag using §-based color support (for §x hex)
            Component tag = Component.empty();
            if (tagStr != null && !tagStr.isBlank()) {
                tag = LegacyComponentSerializer.legacySection().deserialize(tagStr);
                if (!tagStr.endsWith(" ")) tag = tag.append(Component.space());
            }

            // Deserialize prefix and suffix using legacy &-based color (with hex support)
            Component prefix = MessageUtil.legacyColor(prefixStr);
            if (!prefixStr.endsWith(" ")) prefix = prefix.append(Component.space());

            Component name = player.displayName(); // May have nickname or custom formatting
            Component suffix = Component.empty();
            if (suffixStr != null && !suffixStr.isBlank()) {
                suffix = MessageUtil.legacyColor(suffixStr);
                if (!suffixStr.startsWith(" ")) suffix = Component.space().append(suffix);
            }

            // Compose the full chat line
            Component full = Component.empty()
                    .append(tag)                      // Optional tag
                    .append(prefix.append(name))      // Name inherits prefix color
                    .append(suffix)                   // Optional suffix
                    .append(Component.text(": ", NamedTextColor.WHITE))
                    .append(message);                 // GG-styled component

            long now = System.currentTimeMillis();
            long lastUsed = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (!(now - lastUsed < 15_000)) {
                String rewardCommand = config.config().getString("core.ggwave.reward-command")
                        .replace("{player}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
                player.sendMessage(config.getMessage("core.ggwave.reward-message"));
                cooldowns.put(player.getUniqueId(), now);
            }
            return full;
        }));

    }

}
