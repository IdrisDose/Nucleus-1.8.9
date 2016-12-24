/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.dataservices.KitService;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.inject.Inject;

@Permissions(prefix = "firstjoinkit")
@NoCost
@NoCooldown
@NoWarmup
@RegisterCommand(value = "redeem", subcommandOf = FirstKitCommand.class)
public class FirstKitRedeemCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject
    private KitService gds;

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        int reject = 0;

        Inventory target = Util.getStandardInventory(src);
        for (ItemStackSnapshot x : gds.getFirstKit()) {
            if (x.getType() != ItemTypes.NONE) {
                reject += target.offer(x.createStack()).getRejectedItems().size();
            }
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.redeem.success"));
        if (reject > 0) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.redeem.reject"));
        }

        return CommandResult.success();
    }
}
