/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Toggles whether powetools will activate for this user.
 *
 * Permission: plugin.powertool.base (uses the base permission)
 */
@Permissions(mainOverride = "powertool")
@RunAsync
@NoCooldown
@NoWarmup
@NoCost
@RegisterCommand(value = {"toggle"}, subcommandOf = PowertoolCommand.class)
public class TogglePowertoolCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String toggleKey = "toggle";
    @Inject private UserDataManager loader;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.bool(Text.of(toggleKey))))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        UserService user = loader.get(src).get();

        // If specified - get the key. Else, the inverse of what we have now.
        boolean toggle = args.<Boolean>getOne(toggleKey).orElse(!user.isPowertoolToggled());
        user.setPowertoolToggle(toggle);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.powertool.toggle",
                plugin.getMessageProvider().getMessageWithFormat(toggle ? "standard.enabled" : "standard.disabled")));
        return CommandResult.success();
    }
}
