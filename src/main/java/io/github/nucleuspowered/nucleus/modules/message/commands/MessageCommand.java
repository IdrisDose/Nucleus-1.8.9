/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.NicknameArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NotifyIfAFK;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.message.handlers.MessageHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages a player.
 */
@Permissions(suggestedLevel = SuggestedLevel.USER, supportsSelectors = true)
@RunAsync
@RegisterCommand(value = { "message", "m", "msg", "whisper", "w", "t" }, rootAliasRegister = { "tell" })
@NotifyIfAFK(MessageCommand.to)
public class MessageCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {
    static final String to = "to";
    private final String message = "message";

    private final MessageHandler handler;

    /**
     * Testing only.
     */
    public MessageCommand() {
        handler = new MessageHandler();
    }

    @Inject
    private MessageCommand(MessageHandler handler) {
        this.handler = handler;
        handler.setCommandPermissionHandler(this::getPermissionHandler);
    }

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> mp = new HashMap<>();
        mp.put("color", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.message.color"), SuggestedLevel.ADMIN));
        mp.put("colour", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.message.colour"), SuggestedLevel.ADMIN));
        mp.put("style", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.message.style"), SuggestedLevel.ADMIN));
        mp.put("magic", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.message.magic"), SuggestedLevel.ADMIN));
        mp.put("url", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.message.urls"), SuggestedLevel.ADMIN));
        return mp;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            new SelectorWrapperArgument(
                new NicknameArgument(Text.of(to), plugin.getUserDataManager(), NicknameArgument.UnderlyingType.PLAYER_CONSOLE),
                permissions,
                SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS),
            GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(message)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        boolean b = handler.sendMessage(src, args.<CommandSource>getOne(to).get(), args.<String>getOne(message).get());
        return b ? CommandResult.success() : CommandResult.empty();
    }
}
