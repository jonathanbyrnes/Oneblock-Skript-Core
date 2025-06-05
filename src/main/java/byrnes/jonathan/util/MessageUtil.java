package byrnes.jonathan.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtil {
    public static Component color(String text) {
        return MiniMessage.miniMessage().deserialize(text != null ? text : "<red>Missing message.");
    }
}
