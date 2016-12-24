/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.HomeOtherArgument;
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

@Permissions(prefix = "home", mainOverride = "other", suggestedLevel = SuggestedLevel.MOD)
@RegisterCommand("homeother")
public class HomeOtherCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String home = "home";

    @Inject private CoreConfigAdapter cca;
    @Inject private HomeConfigAdapter homeConfigAdapter;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(new HomeOtherArgument(Text.of(home), plugin, cca))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        // Get the home.
        HomeOtherArgument.HomeData wl = args.<HomeOtherArgument.HomeData>getOne(home).get();

        if (!wl.location.getLocation().isPresent()) {
            // Fail
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.homeother.invalid", wl.user.getName(), wl.location.getName()));
            return CommandResult.empty();
        }

        // Warp to it safely.
        if (plugin.getTeleportHandler().teleportPlayer(src, wl.location.getLocation().get(), wl.location.getRotation(), homeConfigAdapter.getNodeOrDefault().isSafeTeleport())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.homeother.success", wl.user.getName(), wl.location.getName()));
            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.homeother.fail", wl.user.getName(), wl.location.getName()));
            return CommandResult.empty();
        }
    }
}
