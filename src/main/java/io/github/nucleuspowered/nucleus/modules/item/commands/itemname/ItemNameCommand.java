/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.itemname;

import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@RegisterCommand(value = "itemname", hasExecutor = false)
public class ItemNameCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {
    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        return null;
    }
}
