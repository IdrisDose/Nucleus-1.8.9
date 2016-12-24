/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Permissions
@RegisterCommand("sudo")
public class SudoCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String playerKey = "player";
    private final String commandKey = "command";

    private final TextTemplate chatTemplate = TextTemplate.of(TextTemplate.arg(MessageEvent.PARAM_MESSAGE_HEADER).build(),
            TextTemplate.arg(MessageEvent.PARAM_MESSAGE_BODY), TextTemplate.arg(MessageEvent.PARAM_MESSAGE_FOOTER));

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.onlyOne(GenericArguments.player(Text.of(playerKey))),
                GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(commandKey)))
        };
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("exempt.target", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.sudo.exempt"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = args.<Player>getOne(playerKey).get();
        String cmd = args.<String>getOne(commandKey).get();
        if (pl.equals(src) || permissions.testSuffix(pl, "exempt.target")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sudo.noperms"));
            return CommandResult.empty();
        }

        if (cmd.startsWith("c:")) {
            if (cmd.equals("c:")) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sudo.chatfail"));
                return CommandResult.empty();
            }

            Text rawMessage = Text.of(cmd.split(":", 2)[1]);
            MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(
                    Cause.source(pl).named(NamedCause.notifier(src)).build(),
                    pl.getMessageChannel(),
                    Optional.of(pl.getMessageChannel()),
                    new MessageEvent.MessageFormatter(Text.builder("<" + pl.getName() + "> ")
                            .onShiftClick(TextActions.insertText(pl.getName()))
                            .onClick(TextActions.suggestCommand("/msg " + pl.getName()))
                            .build(), rawMessage),
                    rawMessage,
                    false);

            if (!Sponge.getEventManager().post(event)) {
                MessageEvent.MessageFormatter formatter = event.getFormatter();
                pl.getMessageChannel().send(pl, chatTemplate.apply(ImmutableMap.of(MessageEvent.PARAM_MESSAGE_HEADER, formatter.getHeader(),
                        MessageEvent.PARAM_MESSAGE_BODY, formatter.getBody(),
                        MessageEvent.PARAM_MESSAGE_FOOTER, formatter.getFooter())).build());
                return CommandResult.success();
            }

            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sudo.chatcancelled"));
            return CommandResult.empty();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sudo.force", pl.getName(), cmd));
        Sponge.getCommandManager().process(pl, cmd);
        return CommandResult.success();
    }
}
