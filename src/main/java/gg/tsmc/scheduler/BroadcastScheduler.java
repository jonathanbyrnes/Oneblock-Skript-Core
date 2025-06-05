package gg.tsmc.scheduler;

import gg.tsmc.config.ConfigHelper;
import gg.tsmc.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class BroadcastScheduler {

    private final Plugin plugin;
    private final ConfigHelper config;
    private final List<List<String>> broadcasts = new ArrayList<>();
    private int index = 0;
    private int intervalTicks;
    private BukkitRunnable task;

    public BroadcastScheduler(Plugin plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void start() {
        reload(); // load messages and interval

        stop(); // cancel existing if needed
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (broadcasts.isEmpty()) return;

                List<String> lines = broadcasts.get(index);
                lines.forEach(line -> Bukkit.broadcast(MessageUtil.color(line)));

                index = (index + 1) % broadcasts.size(); // cycle to next
            }
        };
        task.runTaskTimer(plugin, 0, intervalTicks); // run every interval
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reload() {
        this.broadcasts.clear();
        this.index = 0;

        List<?> rawList = config.config().getList("broadcasts.messages");
        if (rawList != null) {
            for (Object entry : rawList) {
                if (entry instanceof List<?>) {
                    List<String> section = ((List<?>) entry).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList();
                    if (!section.isEmpty()) broadcasts.add(section);
                }
            }
        }

        int seconds = config.config().getInt("broadcasts.interval-seconds", 120);
        this.intervalTicks = seconds * 20;
    }
}
