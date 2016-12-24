/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.misc.config.MiscConfigAdapter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

@RegisterCommand("speed")
@Permissions(supportsSelectors = true)
public class SpeedCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String speedKey = "speed";
    private final String typeKey = "type";
    private final String playerKey = "player";

    @Inject private MiscConfigAdapter miscConfigAdapter;

    /**
     * As the standard flying speed is 0.05 and the standard walking speed is
     * 0.1, we multiply it by 20 and use integers. Standard walking speed is
     * therefore 2, standard flying speed - 1.
     */
    public static final int multiplier = 20;

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> mspi = Maps.newHashMap();
        mspi.put("exempt.max", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.speed.exempt.max"), SuggestedLevel.NONE));
        return mspi;
    }

    @Override
    public CommandElement[] getArguments() {
        Map<String, SpeedType> keysMap = new HashMap<>();
        keysMap.put("fly", SpeedType.FLYING);
        keysMap.put("flying", SpeedType.FLYING);
        keysMap.put("f", SpeedType.FLYING);

        keysMap.put("walk", SpeedType.WALKING);
        keysMap.put("w", SpeedType.WALKING);

        return new CommandElement[] {GenericArguments.optional(GenericArguments.seq(
                GenericArguments.optionalWeak(GenericArguments.onlyOne(
                        GenericArguments.requiringPermission(
                            new SelectorWrapperArgument(GenericArguments.player(Text.of(playerKey)), permissions, SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS)
                            , permissions.getPermissionWithSuffix("others")))),
                GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.choices(Text.of(typeKey), keysMap, true))),
                GenericArguments.integer(Text.of(speedKey))))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = this.getUserFromArgs(Player.class, src, playerKey, args);
        Optional<Integer> ospeed = args.getOne(speedKey);
        if (!ospeed.isPresent()) {
            Text t = Text.builder().append(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.walk")).append(Text.of(" "))
                    .append(Text.of(TextColors.YELLOW, Math.round(pl.get(Keys.WALKING_SPEED).orElse(0.1d) * 20)))
                    .append(Text.builder().append(Text.of(TextColors.GREEN, ", ")).append(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.flying"))
                            .build())
                    .append(Text.of(" ")).append(Text.of(TextColors.YELLOW, Math.round(pl.get(Keys.FLYING_SPEED).orElse(0.05d) * 20)))
                    .append(Text.of(TextColors.GREEN, ".")).build();

            src.sendMessage(t);

            // Don't trigger cooldowns
            return CommandResult.empty();
        }

        SpeedType key = args.<SpeedType>getOne(typeKey).orElseGet(() -> pl.get(Keys.IS_FLYING).orElse(false) ? SpeedType.FLYING : SpeedType.WALKING);
        int speed = ospeed.get();

        if (speed < 0) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.negative"));
            return CommandResult.empty();
        }

        int maxSpeed = miscConfigAdapter.getNodeOrDefault().getMaxSpeed();
        if (!permissions.testSuffix(src, "exempt.max") && maxSpeed < speed) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.max", String.valueOf(maxSpeed)));
            return CommandResult.empty();
        }

        DataTransactionResult dtr = pl.offer(key.speedKey, (double) speed / (double) multiplier);

        if (dtr.isSuccessful()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.success.base", key.name, String.valueOf(speed)));

            if (!pl.equals(src)) {
                src.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.success.other", pl.getName(), key.name, String.valueOf(speed)));
            }

            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.speed.fail", key.name));
        return CommandResult.empty();
    }

    private enum SpeedType {
        WALKING(Keys.WALKING_SPEED, Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("standard.walking")),
        FLYING(Keys.FLYING_SPEED, Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("standard.flying"));

        final Key<Value<Double>> speedKey;
        final String name;

        SpeedType(Key<Value<Double>> speedKey, String name) {
            this.speedKey = speedKey;
            this.name = name;
        }
    }
}
