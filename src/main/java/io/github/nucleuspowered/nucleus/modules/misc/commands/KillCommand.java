/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
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

@Permissions(supportsSelectors = true)
@RegisterCommand("kill")
public class KillCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String key = "player";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            new SelectorWrapperArgument(GenericArguments.player(Text.of(key)), permissions, SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS)
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = args.<Player>getOne(key).get();
        GameMode gm = pl.gameMode().getDirect().orElse(pl.gameMode().getDefault());
        if (gm != GameModes.SURVIVAL && gm != GameModes.NOT_SET) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.wronggm", pl.getName()));
            return CommandResult.empty();
        }

        pl.offer(Keys.HEALTH, 0d);
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.killed", pl.getName()));
        pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kill.killedby", src.getName()));

        return CommandResult.success();
    }
}
