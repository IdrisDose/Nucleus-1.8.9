/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.teleport.handlers.TeleportHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * /tpdeny.
 */
@Permissions(prefix = "teleport", suggestedLevel = SuggestedLevel.USER)
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"tpdeny", "teleportdeny"})
public class TeleportDenyCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private TeleportHandler teleportHandler;

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        boolean denied = teleportHandler.remove(src.getUniqueId());
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat(denied ? "command.tpdeny.deny" : "command.tpdeny.fail"));
        return CommandResult.success();
    }
}
