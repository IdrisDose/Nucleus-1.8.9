/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Teleports you to the world spawn.
 *
 * Command Usage: /world spawn Permission: nucleus.world.spawn.base
 */
@Permissions(prefix = "world", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"spawn"}, subcommandOf = WorldCommand.class)
public class WorldSpawnCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Override
    public CommandResult executeCommand(Player pl, CommandContext args) throws Exception {
        pl.setLocation(pl.getWorld().getSpawnLocation());
        pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.spawn.success"));
        return CommandResult.success();
    }
}
