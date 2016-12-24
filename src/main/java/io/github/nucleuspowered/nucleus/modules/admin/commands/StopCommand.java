/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

@Permissions
@NoCooldown
@NoWarmup
@NoCost
@RegisterCommand({"stop"})
public class StopCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String messageKey = "message";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optional(
                        GenericArguments.remainingJoinedStrings(Text.of(messageKey)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<String> opt = args.getOne(messageKey);
        if (opt.isPresent()) {
            Sponge.getServer().shutdown(TextSerializers.FORMATTING_CODE.deserialize(opt.get()));
        } else {
            Sponge.getServer().shutdown();
        }

        return CommandResult.success();
    }
}
