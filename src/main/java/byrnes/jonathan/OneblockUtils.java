package byrnes.jonathan;

import byrnes.jonathan.command.*;
import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.listener.*;
import byrnes.jonathan.manager.DropLockManager;
import byrnes.jonathan.manager.GgWaveManager;
import byrnes.jonathan.scheduler.BroadcastScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class OneblockUtils extends JavaPlugin {

    private BroadcastScheduler broadcastScheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        LegacyPaperCommandManager<CommandSender> commandManager = LegacyPaperCommandManager.createNative(this, ExecutionCoordinator.simpleCoordinator());
        AnnotationParser<CommandSender> parser = new AnnotationParser<>(commandManager, CommandSender.class);

        // Register config
        ConfigHelper configHelper = new ConfigHelper(this);

        GgWaveManager ggManager = new GgWaveManager();
        DropLockManager dropLockManager = new DropLockManager();

        // Register schedulers
        this.broadcastScheduler = new BroadcastScheduler(this, configHelper);
        broadcastScheduler.start();

        // Register listeners
        CoreListener coreListener = new CoreListener(configHelper);
        getServer().getPluginManager().registerEvents(coreListener, this);
        getServer().getPluginManager().registerEvents(new GgWaveListener(ggManager, configHelper, this), this);
        getServer().getPluginManager().registerEvents(new DropLockListener(dropLockManager,configHelper), this);
        getServer().getPluginManager().registerEvents(new CreateIslandListener(this), this);
        getServer().getPluginManager().registerEvents(new ResizeListener(configHelper), this);

        // Register commands
        parser.parse(new KeyAllCommand(configHelper));
        parser.parse(new ApplyEffectCommand(configHelper));
        parser.parse(new BroadcastsCommand(configHelper, broadcastScheduler));
        parser.parse(new BaltopCommand(configHelper));
        parser.parse(new FruitcoinCommand(configHelper));
        parser.parse(new MiscInfoCommand(configHelper));
        parser.parse(new SellCommand());
        parser.parse(new WelcomeCommand(configHelper, coreListener));
        parser.parse(new GgWaveCommand(ggManager, this));
        parser.parse(new ResizeCommand(configHelper));
        parser.parse(new ClaimWeeklyRewardCommand(configHelper));
        parser.parse(new ClaimWeeklyBoosterCommand(configHelper));
        parser.parse(new SeasonalKeyCommand(configHelper));
        parser.parse(new StaffCommand());
        parser.parse(new DropLockCommand(dropLockManager,configHelper));

    }

    @Override
    public void onDisable() {
        //Cleanup Broadcasts
        if (broadcastScheduler != null) {
            broadcastScheduler.stop();
        }

    }

}
