package byrnes.jonathan.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {

    public static Component color(String text) {
        return MiniMessage.miniMessage().deserialize(text != null ? text : "<red>Missing message.");
    }

    public static Component legacyColor(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build()
                .deserialize(text);
    }

}
