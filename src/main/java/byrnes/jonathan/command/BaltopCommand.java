package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Command("baltop")
public class BaltopCommand {

    private final ConfigHelper config;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public BaltopCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("")
    @Permission("oneblockutils.baltop.view")
    public void onBaltop(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();

        if (sender instanceof Player player && !player.hasPermission("oneblockutils.baltop.bypass")) {
            if (!checkAndApplyCooldown(player)) {
                sender.sendMessage(config.getMessage("baltop.messages.cooldown"));
                return;
            }
        }

        List<String> names = config.config().getStringList("baltop.placeholder-names");
        List<String> balances = config.config().getStringList("baltop.placeholder-balances");
        String title = config.config().getString("baltop.title");
        String format = config.config().getString("baltop.format");
        String fallback = config.config().getString("baltop.message-empty");

        if (names.isEmpty() || balances.isEmpty() || names.size() != balances.size()) {
            sender.sendMessage(MessageUtil.color(fallback));
            return;
        }

        if (title != null && !title.isBlank()) {
            sender.sendMessage(MessageUtil.color(title));
        }

        for (int i = 0; i < names.size(); i++) {
            String name = PlaceholderAPI.setPlaceholders(null, names.get(i));
            String value = PlaceholderAPI.setPlaceholders(null, balances.get(i));

            if (name.isBlank() || name.equalsIgnoreCase("null")) continue;

            String line = format
                    .replace("{position}", String.valueOf(i + 1))
                    .replace("{name}", name)
                    .replace("{balance}", value);

            sender.sendMessage(MessageUtil.color(line));
        }
    }

    @Command("reload")
    @Permission("oneblockutils.baltop.reload")
    public void reloadCommand(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("baltop.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.baltop.help")
    public void helpCommand(CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("baltop.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    private boolean checkAndApplyCooldown(Player player) {
        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        long cooldownMs = config.config().getInt("baltop.cooldown-seconds", 300) * 1000L;

        if (now - last < cooldownMs) {
            return false;
        }

        cooldowns.put(player.getUniqueId(), now);
        return true;
    }
}
