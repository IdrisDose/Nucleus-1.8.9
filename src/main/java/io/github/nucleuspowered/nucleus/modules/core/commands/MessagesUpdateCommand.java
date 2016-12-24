/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.messages.ConfigMessageProvider;
import io.github.nucleuspowered.nucleus.internal.messages.MessageProvider;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

@RunAsync
@NoCooldown
@NoCost
@NoWarmup
@Permissions(prefix = "nucleus", suggestedLevel = SuggestedLevel.NONE)
@RegisterCommand(value = "update-messages", subcommandOf = NucleusCommand.class)
public class MessagesUpdateCommand extends AbstractCommand<CommandSource> {

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.flags().flag("y").buildWith(GenericArguments.none())
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        MessageProvider messageProvider = plugin.getMessageProvider();
        if (!(messageProvider instanceof ConfigMessageProvider)) {
            throw new ReturnMessageException(messageProvider.getTextMessageWithFormat("command.nucleus.messageupdate.notfile"));
        }

        ConfigMessageProvider cmp = (ConfigMessageProvider)messageProvider;
        List<String> keys = cmp.checkForMigration();
        if (keys.isEmpty()) {
            src.sendMessage(messageProvider.getTextMessageWithFormat("command.nucleus.messageupdate.nothingtoupdate"));
            return CommandResult.empty();
        }

        if (args.hasAny("y")) {
            cmp.reset(keys);
            src.sendMessage(messageProvider.getTextMessageWithFormat("command.nucleus.messageupdate.reset"));
        } else {
            src.sendMessage(messageProvider.getTextMessageWithFormat("command.nucleus.messageupdate.sometoupdate", String.valueOf(keys.size())));
            keys.forEach(x -> src.sendMessage(Text.of(TextColors.YELLOW, x)));
            src.sendMessage(messageProvider.getTextMessageWithFormat("command.nucleus.messageupdate.confirm", "/nucleus update-messages -y").toBuilder()
                .onClick(TextActions.runCommand("/nucleus update-messages -y")).build());
        }

        return CommandResult.success();
    }
}
