/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.JailData;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Permissions(prefix = "jail", suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"checkjail"})
public class CheckJailCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String playerKey = "player";
    @Inject private JailHandler handler;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.user(Text.of(playerKey)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        User user = args.<User>getOne(playerKey).get();
        Optional<JailData> jail = handler.getPlayerJailData(user);

        if (!jail.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkjail.nojail", user.getName()));
            return CommandResult.success();
        }

        JailData md = jail.get();
        String name;
        if (md.getJailer().equals(Util.consoleFakeUUID)) {
            name = Sponge.getServer().getConsole().getName();
        } else {
            Optional<User> ou = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(md.getJailer());
            name = ou.isPresent() ? ou.get().getName() : plugin.getMessageProvider().getMessageWithFormat("standard.unknown");
        }

        String time = "";
        String forString = "";
        if (md.getEndTimestamp().isPresent()) {
            time = Util.getTimeStringFromSeconds(Instant.now().until(md.getEndTimestamp().get(), ChronoUnit.SECONDS));
            forString = " " + plugin.getMessageProvider().getMessageWithFormat("standard.for") + " ";
        } else if (md.getTimeFromNextLogin().isPresent()) {
            time = Util.getTimeStringFromSeconds(md.getTimeFromNextLogin().get().getSeconds());
            forString = " " + plugin.getMessageProvider().getMessageWithFormat("standard.for") + " ";
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkjail.jailed", user.getName(), md.getJailName(), name, forString, time));
        src.sendMessage(Text.of(TextColors.GREEN, MessageFormat.format(plugin.getMessageProvider().getMessageWithFormat("standard.reason"), md.getReason())));
        return CommandResult.success();
    }
}
