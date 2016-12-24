/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.argumentparsers.ImprovedGameModeArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;

@Permissions
@NoCooldown
@NoWarmup
@NoCost
@RegisterCommand({"gamemode", "gm"})
public class GamemodeCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String userKey = "user";
    private final String gamemodeKey = "gamemode";

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> mpi = Maps.newHashMap();
        mpi.put("others", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.gamemode.other"), SuggestedLevel.ADMIN));
        return mpi;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.firstParsing(

                    // <mode>
                    GenericArguments.onlyOne(new ImprovedGameModeArgument(Text.of(gamemodeKey))),

                    // <player> <mode>
                    GenericArguments.seq(
                        GenericArguments.requiringPermission(GenericArguments.onlyOne(GenericArguments.player(Text.of(userKey))), permissions.getPermissionWithSuffix("others")),
                        GenericArguments.onlyOne(new ImprovedGameModeArgument(Text.of(gamemodeKey))))
                )
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player user = this.getUserFromArgs(Player.class, src, userKey, args);
        Optional<GameMode> ogm = args.getOne(gamemodeKey);
        if (!ogm.isPresent()) {
            String mode = user.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL).getName();
            if (src.equals(user)) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.gamemode.get.base", mode));
            } else {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.gamemode.get.other", user.getName(), mode));
            }

            return CommandResult.success();
        }

        GameMode gm = ogm.get();
        DataTransactionResult dtr = user.offer(Keys.GAME_MODE, gm);
        if (dtr.isSuccessful()) {
            if (!src.equals(user)) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.gamemode.set.other", user.getName(), gm.getName()));
            }

            user.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.gamemode.set.base", gm.getName()));
            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.gamemode.error", user.getName()));
        return CommandResult.empty();
    }
}
