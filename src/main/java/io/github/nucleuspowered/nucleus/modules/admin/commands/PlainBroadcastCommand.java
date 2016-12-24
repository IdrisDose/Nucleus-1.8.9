/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.ChatUtil;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

@RunAsync
@NoCooldown
@NoCost
@NoWarmup
@Permissions(suggestedLevel = SuggestedLevel.NONE)
@RegisterCommand({ "plainbroadcast", "pbcast", "pbc" })
public class PlainBroadcastCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {
    private final String message = "message";
    @Inject private ChatUtil chatUtil;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] { GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(message))) };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        MessageChannel.TO_ALL.send(src, chatUtil.addUrlsToAmpersandFormattedString(args.<String>getOne(message).get()));
        return CommandResult.success();
    }
}
