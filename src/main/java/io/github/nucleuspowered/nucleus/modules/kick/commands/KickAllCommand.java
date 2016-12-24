/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kick.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kicks all players
 *
 * Permission: quickstart.kickall.base
 */
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand("kickall")
public class KickAllCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String reason = "reason";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.requiringPermission(GenericArguments.flags().flag("f").buildWith(GenericArguments.none()),
                        permissions.getPermissionWithSuffix("whitelist")),
                GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of(reason))))};
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("whitelist", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.kickall.whitelist"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        String r = args.<String>getOne(reason).orElse(plugin.getMessageProvider().getMessageWithFormat("command.kick.defaultreason"));
        Boolean f = args.<Boolean>getOne("f").orElse(false);

        if (f) {
            Sponge.getServer().setHasWhitelist(true);
        }

        // Don't kick self
        Sponge.getServer().getOnlinePlayers().stream()
                .filter(x -> !(src instanceof Player) || !((Player) src).getUniqueId().equals(x.getUniqueId()))
                .collect(Collectors.toList())
                .forEach(x -> x.kick(TextSerializers.FORMATTING_CODE.deserialize(r)));

        MessageChannel mc = MessageChannel.fixed(Sponge.getServer().getConsole(), src);
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.kickall.message"));
        mc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.reason", r));
        if (f) {
            mc.send(plugin.getMessageProvider().getTextMessageWithFormat("command.kickall.whitelist"));
        }

        return CommandResult.success();
    }
}
