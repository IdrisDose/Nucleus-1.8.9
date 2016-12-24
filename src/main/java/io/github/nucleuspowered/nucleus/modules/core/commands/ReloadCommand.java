/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

@Permissions(prefix = "nucleus")
@NoCooldown
@NoCost
@NoWarmup
@RunAsync
@RegisterCommand(value = "reload", subcommandOf = NucleusCommand.class)
public class ReloadCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        plugin.reload();
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.reload.one"));
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.reload.two"));
        return CommandResult.success();
    }
}
