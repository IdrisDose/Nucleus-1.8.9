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
 * Base command for migration
 */
@RunAsync
@NoCooldown
@NoCost
@NoWarmup
@Permissions(prefix = "nucleus")
@RegisterCommand(value = "migrate", subcommandOf = NucleusCommand.class, hasExecutor = false)
public class MigrateCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        return CommandResult.empty();
    }
}
