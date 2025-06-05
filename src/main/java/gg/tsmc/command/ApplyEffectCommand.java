package gg.tsmc.command;

import gg.tsmc.config.ConfigHelper;
import gg.tsmc.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.Set;

@Command("applyeffect")
public class ApplyEffectCommand {

    private final ConfigHelper config;

    public ApplyEffectCommand(ConfigHelper config) {
        this.config = config;
    }

    @Command("<effect> <player>")
    @Permission("oneblockutils.applyeffect.use")
    public void onApplyEffect(
            CommandContext<CommandSender> context,
            @Argument(value = "effect", suggestions = "perk-effects") final String effectName,
            @Argument("player") final Player player
    ) {
        final CommandSender sender = context.sender();

        if (effectName.equalsIgnoreCase("none")) {
            clearEffects(player);
            player.sendMessage(config.getMessage("effects.messages.effect-cleared"));
            return;
        }

        ConfigurationSection effectSection = config.config().getConfigurationSection("effects." + effectName.toLowerCase());
        if (effectSection == null) {
            sender.sendMessage(config.getMessage("effects.messages.effect-invalid", "{type}", effectName));
            return;
        }

        String permission = effectSection.getString("permission");
        String typeName = effectSection.getString("type");
        int amplifier = effectSection.getInt("amplifier", 0);

        PotionEffectType effectType = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(typeName.toLowerCase()));
        if (effectType == null) {
            sender.sendMessage(Component.text("Invalid potion effect type in config: " + typeName));
            return;
        }

        if (!player.hasPermission(permission)) {
            sender.sendMessage(config.getMessage("effects.messages.effect-not-allowed", "{type}", effectName));
            return;
        }

        applyEffect(player, effectType, amplifier);
        player.sendMessage(config.getMessage("effects.messages.effect-applied", "{type}", effectName));
    }

    @Command("reload")
    @Permission("oneblockutils.applyeffect.reload")
    public void reloadCommand(final CommandContext<CommandSender> context) {
        config.reload();
        context.sender().sendMessage(config.getMessage("effects.messages.reload-success"));
    }

    @Command("help")
    @Permission("oneblockutils.applyeffect.help")
    public void helpCommand(final CommandContext<CommandSender> context) {
        List<String> helpLines = config.getList("effects.messages.help");
        for (String line : helpLines) {
            context.sender().sendMessage(MessageUtil.color(line));
        }
    }

    @Suggestions("perk-effects")
    public List<String> suggestEffectNames(CommandContext<CommandSender> context, String input) {
        ConfigurationSection section = config.config().getConfigurationSection("effects");
        if (section == null) return List.of();
        Set<String> keys = section.getKeys(false);
        return keys.stream()
                .filter(k -> !k.equalsIgnoreCase("messages"))
                .filter(k -> input.isEmpty() || k.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    private void clearEffects(Player player) {
        ConfigurationSection section = config.config().getConfigurationSection("effects");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("messages")) continue;

            String typeName = section.getConfigurationSection(key).getString("type");
            if (typeName == null) continue;

            PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(typeName.toLowerCase()));
            if (type != null) {
                player.removePotionEffect(type);
            }
        }
    }

    private void applyEffect(Player target, PotionEffectType effectType, int amplifier) {
        target.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier, false, false, false));
    }
}
