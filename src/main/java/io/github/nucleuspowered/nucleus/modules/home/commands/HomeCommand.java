/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.data.LocationData;
import io.github.nucleuspowered.nucleus.argumentparsers.HomeArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfigAdapter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Permissions(suggestedLevel = SuggestedLevel.USER)
@RegisterCommand("home")
public class HomeCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String home = "home";

    @Inject private CoreConfigAdapter cca;
    @Inject private HomeConfigAdapter homeConfigAdapter;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.optional(new HomeArgument(Text.of(home), plugin, cca)))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        // Get the home.
        Optional<LocationData> owl = args.getOne(home);
        if (!owl.isPresent()) {
            owl = plugin.getUserDataManager().get(src).get().getHome("home");

            if (!owl.isPresent()) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("args.home.nohome", "home"));
                return CommandResult.empty();
            }
        }

        LocationData wl = owl.get();

        if (!wl.getLocation().isPresent()) {
            // Fail
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.home.invalid", wl.getName()));
            return CommandResult.empty();
        }

        // Warp to it safely.
        if (plugin.getTeleportHandler().teleportPlayer(src, wl.getLocation().get(), wl.getRotation(), homeConfigAdapter.getNodeOrDefault().isSafeTeleport())) {
            if (!wl.getName().equalsIgnoreCase("home")) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.home.success", wl.getName()));
            } else {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.home.successdefault"));
            }

            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.home.fail", wl.getName()));
            return CommandResult.empty();
        }
    }
}
