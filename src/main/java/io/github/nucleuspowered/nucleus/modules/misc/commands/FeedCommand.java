/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

@Permissions(supportsSelectors = true)
@RegisterCommand("feed")
public class FeedCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private static final String player = "player";

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("others", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.others", this.getAliases()[0]), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optionalWeak(GenericArguments.onlyOne(
                GenericArguments.requiringPermission(
                        new SelectorWrapperArgument(
                            GenericArguments.player(Text.of(player)),
                            permissions,
                            SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS), permissions.getPermissionWithSuffix("others"))))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = this.getUserFromArgs(Player.class, src, player, args);

        // Get the food data and modify it.
        FoodData foodData = pl.getFoodData();
        Value<Integer> f = foodData.foodLevel().set(foodData.foodLevel().getDefault());
        Value<Double> d = foodData.saturation().set(foodData.saturation().getDefault());
        foodData.set(f, d);

        if (pl.offer(foodData).isSuccessful()) {
            pl.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat("command.feed.success.self"));
            if (!pl.equals(src)) {
                src.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat("command.feed.success.other", pl.getName()));
            }

            return CommandResult.success();
        } else {
            src.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat("command.feed.error"));
            return CommandResult.empty();
        }
    }
}
