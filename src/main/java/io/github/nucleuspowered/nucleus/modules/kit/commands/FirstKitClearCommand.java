/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.dataservices.KitService;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import javax.inject.Inject;

@Permissions(prefix = "firstjoinkit")
@NoCost
@NoCooldown
@NoWarmup
@RunAsync
@RegisterCommand(value = { "clear", "remove" }, subcommandOf = FirstKitCommand.class)
public class FirstKitClearCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitService gds;

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        gds.setFirstKit(null);
        gds.save();
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.clear.success"));
        return CommandResult.success();
    }
}
