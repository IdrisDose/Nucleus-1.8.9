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
 * Sets spawn of world.
 *
 * Command Usage: /world setspawn Permission: plugin.world.setspawn.base
 */
@Permissions(prefix = "world", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"setspawn"}, subcommandOf = WorldCommand.class)
public class SetSpawnWorldCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Override
    public CommandResult executeCommand(Player pl, CommandContext args) throws Exception {
        pl.getWorld().getProperties().setSpawnPosition(pl.getLocation().getBlockPosition());
        pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.setspawn.success"));
        return CommandResult.success();
    }
}
