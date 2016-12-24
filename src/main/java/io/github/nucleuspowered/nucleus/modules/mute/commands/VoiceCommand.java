/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.mute.handler.MuteHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.util.Map;
import java.util.UUID;

@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@Permissions(prefix = "globalmute")
@RegisterCommand("voice")
public class VoiceCommand extends AbstractCommand<CommandSource> {

    private final String on = "turn on";
    private final String player = "player";

    @Inject
    private MuteHandler muteHandler;

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = super.permissionSuffixesToRegister();
        m.put("auto", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.voice.auto"), SuggestedLevel.ADMIN));
        m.put("notify", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.voice.notify"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.player(Text.of(player)),
                GenericArguments.optional(GenericArguments.bool(Text.of(on)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        if (!muteHandler.isGlobalMuteEnabled()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.globaloff"));
            return CommandResult.empty();
        }

        Player pl = args.<Player>getOne(player).get();
        if (permissions.testSuffix(pl, "auto")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.autovoice", pl.getName()));
            return CommandResult.empty();
        }

        boolean turnOn = args.<Boolean>getOne(on).orElse(!muteHandler.isVoiced(pl.getUniqueId()));

        UUID voice = pl.getUniqueId();
        if (turnOn == muteHandler.isVoiced(voice)) {
            if (turnOn) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.alreadyvoiced", pl.getName()));
            } else {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.alreadynotvoiced", pl.getName()));
            }

            return CommandResult.empty();
        }

        MutableMessageChannel mmc = MessageChannel.permission(permissions.getPermissionWithSuffix("notify")).asMutable();
        mmc.addMember(src);
        if (turnOn) {
            muteHandler.addVoice(pl.getUniqueId());
            mmc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.voiced.source", pl.getName()));
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.voiced.target"));
        } else {
            muteHandler.removeVoice(pl.getUniqueId());
            mmc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.voiced.source", pl.getName()));
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.voice.voiced.target"));
        }

        return CommandResult.success();
    }
}
