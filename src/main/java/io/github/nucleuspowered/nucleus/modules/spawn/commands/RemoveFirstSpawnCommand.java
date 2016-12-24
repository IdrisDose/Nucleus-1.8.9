/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

/**
 * plugin.firstspawn.remove.base
 */
@RegisterCommand(value = {"del", "rm"}, subcommandOf = SetFirstSpawnCommand.class)
@Permissions(prefix = "firstspawn", mainOverride = "remove")
@NoWarmup
@NoCooldown
@NoCost
@RunAsync
public class RemoveFirstSpawnCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private GeneralService data;

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        data.removeFirstSpawn();
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.setfirstspawn.remove"));
        return CommandResult.success();
    }
}
