/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfigAdapter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;

@Permissions(suggestedLevel = SuggestedLevel.USER)
@RegisterCommand("firstspawn")
public class FirstSpawnCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private GeneralService data;
    @Inject private SpawnConfigAdapter spawnConfigAdapter;

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {

        Optional<Transform<World>> olwr = data.getFirstSpawn();
        if (!olwr.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstspawn.notset"));
            return CommandResult.empty();
        }

        if (plugin.getTeleportHandler().teleportPlayer(src, olwr.get(), spawnConfigAdapter.getNodeOrDefault().isSafeTeleport())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstspawn.success"));
            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstspawn.fail"));
        return CommandResult.empty();
    }
}
