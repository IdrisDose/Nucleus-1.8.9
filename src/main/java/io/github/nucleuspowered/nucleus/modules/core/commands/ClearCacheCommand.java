/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import javax.inject.Inject;

/**
 * Clears the {@link UserDataManager} cache, so any offline user's files wll be read on next startup.
 */
@Permissions(prefix = "nucleus")
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand(value = "clearcache", subcommandOf = NucleusCommand.class)
public class ClearCacheCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private UserDataManager ucl;

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        ucl.removeOfflinePlayers();
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.clearcache.success"));
        return CommandResult.success();
    }
}