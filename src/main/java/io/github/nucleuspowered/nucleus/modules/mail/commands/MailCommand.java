/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.MailFilterArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.mail.handlers.MailHandler;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Permissions(suggestedLevel = SuggestedLevel.USER)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand("mail")
public class MailCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final MailReadBase base;
    private final MailHandler handler;

    @Inject
    public MailCommand(MailHandler handler, Game game) {
        base = new MailReadBase(game, handler);
        this.handler = handler;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] { GenericArguments.optional(GenericArguments.allOf(new MailFilterArgument(Text.of(MailReadBase.filters), handler))) };
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        return base.executeCommand(src, src, args.getAll(MailReadBase.filters));
    }
}
