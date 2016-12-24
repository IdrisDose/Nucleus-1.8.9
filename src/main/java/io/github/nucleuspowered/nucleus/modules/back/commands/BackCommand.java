/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back.commands;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.back.handlers.BackHandler;
import io.github.nucleuspowered.nucleus.modules.back.listeners.BackListeners;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

@Permissions
@RegisterCommand({"back", "return"})
public class BackCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private BackHandler handler;

    @Override protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = Maps.newHashMap();
        m.put(BackListeners.onDeath, new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.back.ondeath"), SuggestedLevel.USER));
        m.put(BackListeners.onTeleport, new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.back.onteleport"), SuggestedLevel.USER));
        m.put(BackListeners.onPortal, new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.back.onportal"), SuggestedLevel.USER));
        return m;
    }

    @Override
    @SuppressWarnings("deprecation")
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        Optional<Transform<World>> ol = handler.getLastLocation(src);
        if (!ol.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.back.noloc"));
            return CommandResult.empty();
        }

        Transform<World> loc = ol.get();
        if (plugin.getTeleportHandler().teleportPlayer(src, loc)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.back.success"));
            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.back.nosafe"));
            return CommandResult.empty();
        }
    }
}
