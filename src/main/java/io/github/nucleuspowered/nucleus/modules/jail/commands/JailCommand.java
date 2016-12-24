/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.JailData;
import io.github.nucleuspowered.nucleus.api.data.LocationData;
import io.github.nucleuspowered.nucleus.argumentparsers.JailArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.TimespanArgument;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Permissions(suggestedLevel = SuggestedLevel.MOD, supportsSelectors = true)
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"jail", "unjail", "togglejail"})
public class JailCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    public static final String notifyPermission = PermissionRegistry.PERMISSIONS_PREFIX + "jail.notify";

    @Inject private JailHandler handler;
    private final String playerKey = "player";
    private final String jailKey = "jail";
    private final String durationKey = "duration";
    private final String reasonKey = "reason";

    @Override
    public Map<String, PermissionInformation> permissionsToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put(notifyPermission, new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.jail.notify"), SuggestedLevel.MOD));
        return m;
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("offline", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.jail.offline"), SuggestedLevel.MOD));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.onlyOne(new SelectorWrapperArgument(GenericArguments.user(Text.of(playerKey)), permissions, SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS)),
            GenericArguments.optional(GenericArguments.onlyOne(new JailArgument(Text.of(jailKey), handler))),
            GenericArguments.optionalWeak(GenericArguments.onlyOne(new TimespanArgument(Text.of(durationKey)))),
            GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(reasonKey))))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        // Get the player.
        User pl = args.<User>getOne(playerKey).get();
        if (!pl.isOnline() && !permissions.testSuffix(src, "offline")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.offline.noperms"));
            return CommandResult.empty();
        }

        if (handler.isPlayerJailed(pl)) {
            return onUnjail(src, args, pl);
        } else {
            return onJail(src, args, pl);
        }
    }

    private CommandResult onUnjail(CommandSource src, CommandContext args, User user) {
        if (handler.unjailPlayer(user)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.unjail.success", user.getName()));
            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.unjail.fail", user.getName()));
            return CommandResult.empty();
        }
    }

    private CommandResult onJail(CommandSource src, CommandContext args, User user) {
        Optional<LocationData> owl = args.getOne(jailKey);
        if (!owl.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.jail.nojail"));
            return CommandResult.empty();
        }

        // This might not be there.
        Optional<Long> duration = args.getOne(durationKey);
        String reason = args.<String>getOne(reasonKey).orElse(plugin.getMessageProvider().getMessageWithFormat("command.jail.reason"));
        JailData jd;
        Text message;
        Text messageTo;
        if (duration.isPresent()) {
            if (user.isOnline()) {
                jd = new JailData(Util.getUUID(src), owl.get().getName(), reason, user.getPlayer().get().getLocation(),
                        Instant.now().plusSeconds(duration.get()));
            } else {
                jd = new JailData(Util.getUUID(src), owl.get().getName(), reason, null, Duration.of(duration.get(), ChronoUnit.SECONDS));
            }

            message = plugin.getMessageProvider().getTextMessageWithFormat("command.checkjail.jailed", user.getName(), owl.get().getName(), src.getName(),
                    " " + plugin.getMessageProvider().getMessageWithFormat("standard.for"), " " + Util.getTimeStringFromSeconds(duration.get()));
            messageTo = plugin.getMessageProvider().getTextMessageWithFormat("command.jail.jailed", owl.get().getName(), src.getName(),
                    " " + plugin.getMessageProvider().getMessageWithFormat("standard.for"), " " + Util.getTimeStringFromSeconds(duration.get()));
        } else {
            jd = new JailData(Util.getUUID(src), owl.get().getName(), reason, user.isOnline() ? user.getPlayer().get().getLocation() : null);
            message = plugin.getMessageProvider().getTextMessageWithFormat("command.checkjail.jailed", user.getName(), owl.get().getName(), src.getName(), "", "");
            messageTo = plugin.getMessageProvider().getTextMessageWithFormat("command.jail.jailed", owl.get().getName(), src.getName(), "", "");
        }

        if (handler.jailPlayer(user, jd)) {
            MutableMessageChannel mc = MessageChannel.permission(notifyPermission).asMutable();
            mc.addMember(src);
            mc.send(message);
            mc.send(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", reason));

            if (user.isOnline()) {
                user.getPlayer().get().sendMessage(messageTo);
                user.getPlayer().get().sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", reason));
            }

            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.jail.error"));
        return CommandResult.empty();
    }
}
