/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * nucleus.firstspawn.set.base
 */
@RegisterCommand({"setfirstspawn"})
@Permissions(prefix = "firstspawn", mainOverride = "set")
@NoWarmup
@NoCooldown
@NoCost
@RunAsync
public class SetFirstSpawnCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private GeneralService data;

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        data.setFirstSpawn(src.getLocation(), src.getRotation());
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.setfirstspawn.success"));
        return CommandResult.success();
    }
}
