package byrnes.jonathan.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

@Command("sell")
public class SellCommand {

    @Command("")
    @Permission("oneblockutils.sell.use")
    public void onSellDefault(CommandContext<CommandSender> context) {
        if (context.sender() instanceof Player player) {
            player.performCommand("sellgui");
        }
    }

    @Command("<option>")
    @Permission("oneblockutils.sell.use")
    public void onSellOption(
            CommandContext<CommandSender> context,
            @Argument(value = "option", suggestions = "sell-options") String option
    ) {
        if (!(context.sender() instanceof Player player)) return;

        switch (option.toLowerCase()) {
            case "hand" -> player.performCommand("shopguiplus:sell hand");
            case "all" -> player.performCommand("shopguiplus:sell all");
            default -> player.performCommand("sellgui"); // fallback
        }
    }

    @Suggestions("sell-options")
    public List<String> suggestOptions(CommandContext<CommandSender> context, String input) {
        return List.of("hand", "all").stream()
                .filter(opt -> input.isEmpty() || opt.startsWith(input.toLowerCase()))
                .toList();
    }
}
