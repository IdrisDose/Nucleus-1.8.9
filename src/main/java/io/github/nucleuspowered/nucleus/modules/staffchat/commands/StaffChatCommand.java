/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

@Permissions(suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"staffchat", "sc", "a"})
public class StaffChatCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String message = "message";

    @Inject private UserDataManager loader;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of(message)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<String> toSend = args.getOne(message);
        StaffChatMessageChannel scmc = StaffChatMessageChannel.getInstance();
        if (toSend.isPresent()) {
            scmc.send(src, TextSerializers.FORMATTING_CODE.deserialize(toSend.get()));
            return CommandResult.success();
        }

        if (!(src instanceof Player)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.staffchat.consoletoggle"));
            return CommandResult.empty();
        }

        boolean result;
        UserService user = loader.get((Player)src).get();
        result = !user.isInStaffChat();
        user.setInStaffChat(result);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.staffchat." + (result ? "on" : "off")));
        return CommandResult.success();
    }
}
