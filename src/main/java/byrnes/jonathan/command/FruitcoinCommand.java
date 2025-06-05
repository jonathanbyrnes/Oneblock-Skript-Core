package byrnes.jonathan.command;

import byrnes.jonathan.config.ConfigHelper;
import byrnes.jonathan.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command("fruitcoin")
public class FruitcoinCommand {

    private final ConfigHelper config;

    public FruitcoinCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("")
    @Permission("oneblockutils.fruitcoin.use")
    public void openMenu(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();
        if (sender instanceof Player player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open coins-menu " + player.getName());
        }
    }

    @Command("store")
    @Permission("oneblockutils.fruitcoin.use")
    public void openStore(CommandContext<CommandSender> context) {
        openMenu(context);
    }

    @Command("pay <target> <amount>")
    @Permission("oneblockutils.fruitcoin.pay")
    public void payFruitcoins(
            CommandContext<CommandSender> context,
            @Argument(value = "target", suggestions = "online-players") Player target,
            @Argument(value = "amount", suggestions = "amount-options") String amount
    ) {
        CommandSender sender = context.sender();
        if (amount == null || amount.isBlank()) {
            sender.sendMessage(config.getMessage("fruitcoin.messages.missing-amount"));
            return;
        }

        Bukkit.dispatchCommand(sender, "points pay " + target.getName() + " " + amount);
    }

    @Command("help")
    @Permission("oneblockutils.fruitcoin.help")
    public void showHelp(CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("fruitcoin.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    @Command("reload")
    @Permission("oneblockutils.fruitcoin.reload")
    public void reloadConfig(CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("fruitcoin.messages.reload-success"));
    }

    @Suggestions("online-players")
    public List<String> suggestPlayers(CommandContext<CommandSender> context, String input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> input.isEmpty() || name.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Suggestions("amount-options")
    public List<String> suggestAmounts(CommandContext<CommandSender> context, String input) {
        List<String> options = List.of("1", "10", "25", "50", "100", "250", "500", "1000");
        return options.stream()
                .filter(a -> input.isEmpty() || a.startsWith(input))
                .collect(Collectors.toList());
    }

    @Suggestions("fruitcoin-subcommands")
    public List<String> suggestSubcommands(CommandContext<CommandSender> context, String input) {
        return Stream.of("pay", "store", "help")
                .filter(s -> input.isEmpty() || s.startsWith(input.toLowerCase()))
                .toList();
    }
}
