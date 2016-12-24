/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands.itemalias;

import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.modules.core.commands.NucleusCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Permissions(prefix = "nucleus")
@RegisterCommand(value = {"itemalias", "item"}, subcommandOf = NucleusCommand.class, hasExecutor = false)
public class ItemAliasCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {
    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        return CommandResult.empty();
    }
}
