/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

/**
 * Saves the data files.
 *
 * Permission: plugin.plugin.save
 */
@RunAsync
@NoCooldown
@NoCost
@NoWarmup
@Permissions(prefix = "nucleus")
@RegisterCommand(value = "save", subcommandOf = NucleusCommand.class)
public class SaveCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.save.start"));
        plugin.saveData();
        return CommandResult.success();
    }
}
