/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.MuteData;
import io.github.nucleuspowered.nucleus.argumentparsers.TimespanArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.mute.handler.MuteHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Mutes or unmutes a player.
 *
 * Command Usage: /mute user [time] [reason] Permission: quickstart.mute.base
 * Notify: quickstart.mute.notify
 */
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"mute", "unmute"})
public class MuteCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private MuteConfigAdapter mca;
    @Inject private MuteHandler handler;

    private static String mutedChatPermission = null;

    public static String getMutedChatPermission() {
        return mutedChatPermission;
    }

    private String playerArgument = "player";
    private String timespanArgument = "time";
    private String reason = "reason";

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("exempt.length", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.mute.exempt.target"), SuggestedLevel.ADMIN));
        m.put("exempt.target", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.mute.exempt.length"), SuggestedLevel.MOD));
        m.put("notify", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.mute.notify"), SuggestedLevel.MOD));
        m.put("seemutedchat", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.mute.seemutedchat"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    protected void afterPostInit() {
        if (mutedChatPermission == null) {
            mutedChatPermission = permissions.getPermissionWithSuffix("seemutedchat");
        }
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.user(Text.of(playerArgument))),
                GenericArguments.onlyOne(GenericArguments.optionalWeak(new TimespanArgument(Text.of(timespanArgument)))),
                GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(reason))))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {

        // Get the user.
        User user = args.<User>getOne(playerArgument).get();

        Optional<Long> time = args.getOne(timespanArgument);
        Optional<MuteData> omd = handler.getPlayerMuteData(user);
        Optional<String> reas = args.getOne(reason);

        if (permissions.testSuffix(user, "exempt.target")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.mute.exempt", user.getName()));
            return CommandResult.success();
        }

        // No time, no reason, but is muted, unmute
        if (omd.isPresent() && !time.isPresent() && !reas.isPresent()) {
            // Unmute.
            handler.unmutePlayer(user);
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.unmute.success", user.getName(), src.getName()));
            return CommandResult.success();
        }

        // Do we have a reason?
        String rs = reas.orElse(plugin.getMessageProvider().getMessageWithFormat("command.mute.defaultreason"));
        UUID ua = Util.consoleFakeUUID;
        if (src instanceof Player) {
            ua = ((Player) src).getUniqueId();
        }

        if (time.orElse(Long.MAX_VALUE) > mca.getNodeOrDefault().getMaximumMuteLength() &&  mca.getNodeOrDefault().getMaximumMuteLength() != -1 && !permissions.testSuffix(src, "exempt.length")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.mute.length.toolong", Util.getTimeStringFromSeconds(mca.getNodeOrDefault().getMaximumMuteLength())));
            return CommandResult.success();
        }

        MuteData data;
        if (time.isPresent()) {
            data = new MuteData(ua, rs, Duration.ofSeconds(time.get()));
        } else {
            data = new MuteData(ua, rs);
        }

        if (handler.mutePlayer(user, data)) {
            // Success.
            MutableMessageChannel mc = MessageChannel.permission(permissions.getPermissionWithSuffix("notify")).asMutable();
            mc.addMember(src);

            if (time.isPresent()) {
                timedMute(src, user, data, time.get(), mc);
            } else {
                permMute(src, user, data, mc);
            }

            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.mute.fail", user.getName()));
        return CommandResult.empty();
    }

    private void timedMute(CommandSource src, User user, MuteData data, long time, MessageChannel mc) {
        String ts = Util.getTimeStringFromSeconds(time);
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.mute.success.time", user.getName(), src.getName(), ts));
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", data.getReason()));

        if (user.isOnline()) {
            user.getPlayer().get().sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("mute.playernotify.time", ts));
            user.getPlayer().get().sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.reason", data.getReason()));
        }
    }

    private void permMute(CommandSource src, User user, MuteData data, MessageChannel mc) {
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.mute.success.norm", user.getName(), src.getName()));
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", data.getReason()));

        if (user.isOnline()) {
            user.getPlayer().get().sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("mute.playernotify.standard"));
            user.getPlayer().get().sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.reason", data.getReason()));
        }
    }
}
