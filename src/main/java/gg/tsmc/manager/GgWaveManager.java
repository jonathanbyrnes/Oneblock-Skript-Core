package gg.tsmc.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GgWaveManager {
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final Random random = new Random();

    // Each entry is "startHex,midHex,endHex"
    private static final List<String> GRADIENTS = List.of(
            "#F44952,#FA989D,#F44952",
            "#F78122,#F8B87D,#F78122",
            "yellow,#FFF5BE,yellow",
            "#86FC3E,#BEF89B,#86FC3E",
            "#50D5FF,#9FE2FF,#50D5FF",
            "#33ADFC,#9FD9FF,#33ADFC",
            "#B95FFF,#D7A7FC,#B95FFF",
            "#FF5DB4,#FAB8DB,#FF5DB4"
    );

    public boolean isActive()       { return active.get(); }
    public void activate()          { active.set(true); }
    public void deactivate()        { active.set(false); }

    /**
     * Build a bold, two‐character "GG" component with a simple
     * start→end gradient. (Mid‐stop is ignored for length=2.)
     */
    public Component getRandomStyledComponent() {
        String[] stops = GRADIENTS.get(random.nextInt(GRADIENTS.size()))
                .split(",", 3);
        TextColor start = TextColor.fromHexString(stops[0]);
        TextColor end   = TextColor.fromHexString(stops[2]);

        // First 'G' = start color, second 'G' = end color.
        Component first  = Component.text("G", start)
                .decorate(TextDecoration.BOLD);
        Component second = Component.text("G", end)
                .decorate(TextDecoration.BOLD);
        return first.append(second);
    }
}
