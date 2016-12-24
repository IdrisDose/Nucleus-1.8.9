/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.NicknameArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

@Permissions(supportsSelectors = true)
@RegisterCommand({"ignite", "burn"})
public class IgniteCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String player = "player";
    private final String ticks = "ticks";

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("others", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.others", this.getAliases()[0]), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.optionalWeak(
                    GenericArguments.requiringPermission(
                        GenericArguments.onlyOne(
                                new SelectorWrapperArgument(
                                    new NicknameArgument(Text.of(player), plugin.getUserDataManager(), NicknameArgument.UnderlyingType.PLAYER),
                                    permissions,
                                    SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS)),
                        permissions.getPermissionWithSuffix("others")
                )),
                GenericArguments.onlyOne(GenericArguments.integer(Text.of(ticks)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource pl, CommandContext args) throws Exception {
        int ticksInput = args.<Integer>getOne(ticks).get();
        Player target = this.getUserFromArgs(Player.class, pl, player, args);
        GameMode gm = target.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL);
        if (gm == GameModes.CREATIVE || gm == GameModes.SPECTATOR) {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.ignite.gamemode", target.getName()));
            return CommandResult.empty();
        }

        if (target.offer(Keys.FIRE_TICKS, ticksInput).isSuccessful()) {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.ignite.success", target.getName(), String.valueOf(ticksInput)));
            return CommandResult.success();
        } else {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.ignite.error", target.getName()));
            return CommandResult.empty();
        }
    }
}
