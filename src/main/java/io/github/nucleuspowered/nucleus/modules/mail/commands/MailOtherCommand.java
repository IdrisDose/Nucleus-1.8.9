/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.MailFilterArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.NicknameArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.modules.mail.handlers.MailHandler;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

@Permissions(prefix = "mail")
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand(value = {"other", "o"}, subcommandOf = MailCommand.class)
public class MailOtherCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final MailReadBase base;
    private final MailHandler handler;
    private final String userKey = "user";

    @Inject
    public MailOtherCommand(MailHandler handler, Game game) {
        base = new MailReadBase(game, handler);
        this.handler = handler;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            new NicknameArgument(Text.of(userKey), plugin.getUserDataManager(), NicknameArgument.UnderlyingType.USER),
            GenericArguments.optional(GenericArguments.allOf(new MailFilterArgument(Text.of(MailReadBase.filters), handler)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        return base.executeCommand(src, args.<User>getOne(userKey).get(), args.getAll(MailReadBase.filters));
    }
}
