/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Permissions(prefix = "jail")
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand(value = "set", subcommandOf = JailsCommand.class)
public class SetJailCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String jailName = "jail";
    @Inject private JailHandler handler;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.string(Text.of(jailName)))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        String name = args.<String>getOne(jailName).get().toLowerCase();
        if (handler.getJail(name).isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jails.set.exists", name));
            return CommandResult.empty();
        }

        if (handler.setJail(name, src.getLocation(), src.getRotation())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jails.set.success", name));
            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jails.set.error", name));
            return CommandResult.empty();
        }
    }
}
