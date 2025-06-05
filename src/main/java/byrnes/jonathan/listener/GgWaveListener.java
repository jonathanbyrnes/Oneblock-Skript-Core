package byrnes.jonathan.listener;

import byrnes.jonathan.manager.GgWaveManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GgWaveListener implements Listener {
    private static final Pattern GG_WORD = Pattern.compile("(?i)\\bgg\\b");
    private final GgWaveManager manager;
    private final Logger logger;

    public GgWaveListener(GgWaveManager manager, Logger logger) {
        this.manager = manager;
        this.logger = logger;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!manager.isActive()) return;

        Component original = event.message();
        // Serialize to plain text so we know exactly what we got
        String plain = PlainTextComponentSerializer.plainText().serialize(original);

        logger.info("[GG DEBUG] received message: \"" + plain + "\"");

        if (!GG_WORD.matcher(plain).find()) {
            logger.info("[GG DEBUG] no standalone ‘gg’ found, skipping");
            return;
        }

        // Do the replace, and log whether it actually changed
        Component replaced = original.replaceText(config -> {
            config.match(GG_WORD)
                    .replacement((match, in) -> {
                        logger.info("[GG DEBUG] matching ‘" + match.group() + "’, inserting gradient");
                        return manager.getRandomStyledComponent();
                    });
        });

        String plainReplaced = PlainTextComponentSerializer.plainText().serialize(replaced);
        logger.info("[GG DEBUG] replaced plain text: \"" + plainReplaced + "\"");
        logger.info("[GG DEBUG] replaced component: " + replaced);

        // Hand it back so your normal chat plugin still prefixes/suffixes
        event.message(replaced);
    }
}
