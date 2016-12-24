/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

@Permissions(prefix = "teleport", suggestedLevel = SuggestedLevel.USER)
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"tptoggle"})
@RunAsync
public class TeleportToggleCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String key = "toggle";
    @Inject private UserDataManager udm;

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("exempt", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.tptoggle.exempt"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.bool(Text.of(key))))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        final UserService iqsu = udm.get(src).get();
        boolean flip = args.<Boolean>getOne(key).orElseGet(() -> !iqsu.isTeleportToggled());
        iqsu.setTeleportToggled(flip);
        src.sendMessage(Text.builder().append(
                plugin.getMessageProvider().getTextMessageWithFormat("command.tptoggle.success", plugin.getMessageProvider().getMessageWithFormat(flip ? "standard.enabled" : "standard.disabled")))
                .build());
        return CommandResult.success();
    }
}
