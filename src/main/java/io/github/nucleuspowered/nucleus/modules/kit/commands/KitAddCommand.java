/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Sets kit items.
 *
 * Command Usage: /kit add Permission: plugin.kit.add.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"add", "createFromInventory"}, subcommandOf = KitCommand.class)
@NoWarmup
@NoCooldown
@NoCost
public class KitAddCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private KitHandler kitConfig;

    private final String name = "name";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.string(Text.of(name)))};
    }

    @Override
    public CommandResult executeCommand(final Player player, CommandContext args) throws Exception {
        String kitName = args.<String>getOne(name).get();

        if (!kitConfig.getKitNames().stream().anyMatch(kitName::equalsIgnoreCase)) {
            kitConfig.saveKit(kitName, kitConfig.createKit().updateKitInventory(player));
            player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.add.success", kitName));
            return CommandResult.success();
        } else {
            player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.add.alreadyexists", kitName));
            return CommandResult.empty();
        }
    }
}
