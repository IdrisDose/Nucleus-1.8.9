/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.GameProfileArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.util.ban.Ban;

import java.util.Optional;

@RegisterCommand({"unban", "pardon"})
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@NoWarmup
@NoCooldown
@NoCost
public class UnbanCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String key = "player";
    private final String key2 = "user";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.firstParsing(
                // GenericArguments.onlyOne(GenericArguments.user(Text.of(key))),
                GenericArguments.onlyOne(new GameProfileArgument(Text.of(key2)))
            )
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        GameProfile gp;
        if (args.<User>getOne(key).isPresent()) {
            gp = args.<User>getOne(key).get().getProfile();
        } else {
            gp = args.<GameProfile>getOne(key2).get();
        }

        BanService service = Sponge.getServiceManager().provideUnchecked(BanService.class);

        Optional<Ban.Profile> obp = service.getBanFor(gp);
        if (!obp.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkban.notset", gp.getName().orElse(plugin.getMessageProvider().getMessageWithFormat("standard.unknown"))));
            return CommandResult.empty();
        }

        service.removeBan(obp.get());

        MutableMessageChannel notify = MessageChannel.permission(BanCommand.notifyPermission).asMutable();
        notify.addMember(src);
        notify.send(plugin.getMessageProvider().getTextMessageWithFormat("command.unban.success", obp.get().getProfile().getName().orElse("standard.unknown"), src.getName()));
        return CommandResult.success();
    }
}
