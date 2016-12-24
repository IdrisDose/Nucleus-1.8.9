/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

@Permissions(suggestedLevel = SuggestedLevel.USER)
@RegisterCommand("suicide")
public class SuicideCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Override
    @SuppressWarnings("deprecation")
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        GameMode gm = src.gameMode().getDirect().orElse(src.gameMode().getDefault());
        if (gm != GameModes.SURVIVAL && gm != GameModes.NOT_SET) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.suicide.wronggm"));
            return CommandResult.empty();
        }

        src.offer(Keys.HEALTH, 0d);
        return CommandResult.success();
    }
}
