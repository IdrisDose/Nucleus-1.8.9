/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.argumentparsers.TimespanArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RegisterCommand("tempban")
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@NoWarmup
@NoCooldown
@NoCost
public class TempBanCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private BanConfigAdapter bca;
    private final String user = "user";
    private final String reasonKey = "reasonKey";
    private final String duration = "duration";

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("offline", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.tempban.offline"), SuggestedLevel.MOD));
        m.put("exempt.target", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.tempban.exempt.target"), SuggestedLevel.MOD));
        m.put("exempt.length", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.tempban.exempt.length"), SuggestedLevel.MOD));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.onlyOne(GenericArguments.user(Text.of(user))), GenericArguments.onlyOne(new TimespanArgument(Text.of(duration))),
                GenericArguments.optionalWeak(GenericArguments.remainingJoinedStrings(Text.of(reasonKey)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        User u = args.<User>getOne(user).get();
        Long time = args.<Long>getOne(duration).get();
        String reason = args.<String>getOne(reasonKey).orElse(plugin.getMessageProvider().getMessageWithFormat("ban.defaultreason"));

        if (permissions.testSuffix(u, "exempt.target")) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.tempban.exempt", u.getName()));
        }

        if (!u.isOnline() && !permissions.testSuffix(src, "offline")) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.tempban.offline.noperms"));
        }

        if (time > bca.getNodeOrDefault().getMaximumTempBanLength() &&  bca.getNodeOrDefault().getMaximumTempBanLength() != -1 && !permissions.testSuffix(src, "exempt.length")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tempban.length.toolong", Util.getTimeStringFromSeconds(bca.getNodeOrDefault().getMaximumTempBanLength())));
            return CommandResult.success();
        }

        BanService service = Sponge.getServiceManager().provideUnchecked(BanService.class);

        if (service.isBanned(u.getProfile())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.ban.alreadyset", u.getName()));
            return CommandResult.empty();
        }

        // Expiration date
        Instant date = Instant.now().plus(time, ChronoUnit.SECONDS);

        // Create the ban.
        Ban bp = Ban.builder().type(BanTypes.PROFILE).profile(u.getProfile()).source(src).expirationDate(date).reason(TextSerializers.FORMATTING_CODE.deserialize(reason)).build();
        service.addBan(bp);

        MutableMessageChannel send = MessageChannel.permission(BanCommand.notifyPermission).asMutable();
        send.addMember(src);
        send.send(plugin.getMessageProvider().getTextMessageWithFormat("command.tempban.applied", u.getName(), Util.getTimeStringFromSeconds(time), src.getName()));
        send.send(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", reason));

        if (Sponge.getServer().getPlayer(u.getUniqueId()).isPresent()) {
            Sponge.getServer().getPlayer(u.getUniqueId()).get().kick(TextSerializers.FORMATTING_CODE.deserialize(reason));
        }

        return CommandResult.success();
    }
}
