/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.data.LocationData;
import io.github.nucleuspowered.nucleus.argumentparsers.JailArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@NoCooldown
@NoCost
@NoWarmup
@Permissions(prefix = "jail", suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@RegisterCommand(value = "info", subcommandOf = JailsCommand.class)
public class JailInfoCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String jailKey = "jail";
    @Inject private JailHandler handler;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(new JailArgument(Text.of(jailKey), handler))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        LocationData wl = args.<LocationData>getOne(jailKey).get();
        src.sendMessage(Text.builder().append(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.info.name"))
                .append(Text.of(": ", TextColors.GREEN, wl.getName())).build());
        src.sendMessage(Text.builder().append(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.info.location"))
                .append(Text.of(": ", TextColors.GREEN, wl.toLocationString())).build());
        return CommandResult.success();
    }
}
