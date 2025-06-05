package gg.tsmc.command;

import gg.tsmc.manager.GgWaveManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

@Command("ggwave")
public class GgWaveCommand {

    private final GgWaveManager manager;
    private final Plugin plugin;

    public GgWaveCommand(GgWaveManager manager, Plugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @Command("start")
    @Permission("oneblockutils.ggwave.admin")
    public void startWave(CommandContext<CommandSender> context) {
        if (manager.isActive()) {
            context.sender().sendMessage("§cGGWave is already active!");
            return;
        }

        manager.activate();
        context.sender().sendMessage("§aGGWave activated for 10 seconds!");

        Bukkit.getScheduler().runTaskLater(plugin, manager::deactivate, 20L * 10);
    }
}
