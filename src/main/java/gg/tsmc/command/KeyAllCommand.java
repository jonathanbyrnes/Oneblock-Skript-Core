package gg.tsmc.command;

import gg.tsmc.config.ConfigHelper;
import gg.tsmc.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("keyall")
public class KeyAllCommand {

    private final ConfigHelper config;

    public KeyAllCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("[keyType] [amount]")
    @Permission("oneblockutils.keyall.use")
    public void onKeyAll(
            final CommandContext<CommandSender> context,
            @Argument(value = "keyType", suggestions = "key-types") final String keyTypeArg,  // nullable
            @Argument("amount") final Integer amountArg                                    // nullable
    ) {
        String keyType = (keyTypeArg == null) ? "epic" : keyTypeArg;
        int amount    = (amountArg   == null) ? 1      : amountArg;
        CommandSender sender = context.sender();

        List<String> validTypes = config.config().getStringList("keyall.crates");
        if (!validTypes.contains(keyType)) {
            sender.sendMessage(config.getMessage("keyall.messages.invalid-key-type", "{keyType}", keyType)
            );
            return;
        }

        dispatchKey(sender, keyType, amount);
    }

    @Command("reload")
    @Permission("oneblockutils.keyall.reload")
    public void reloadCommand(final CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(
                config.getMessage("keyall.messages.reload-success")
        );
    }

    @Command("help")
    @Permission("oneblockutils.keyall.help")
    public void helpCommand(final CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("keyall.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    @Suggestions("key-types")
    public List<String> suggestKeyTypes(CommandContext<CommandSender> context, String input) {
        return config.getList("keyall.crates");
    }

    private void dispatchKey(CommandSender sender, String keyType, int amount) {
        giveOnlinePlayersKey(keyType, amount);
    }

    private void giveOnlinePlayersKey(String keyType, int amount) {
        String command = "crates key giveall " + keyType + " " + amount;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
