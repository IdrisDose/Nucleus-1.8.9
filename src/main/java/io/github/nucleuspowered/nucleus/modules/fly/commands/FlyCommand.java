/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.NicknameArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

@Permissions
@RegisterCommand("fly")
public class FlyCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private static final String player = "player";
    private static final String toggle = "toggle";
    @Inject private UserDataManager udm;

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("others", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.others", this.getAliases()[0]), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.requiringPermission(
                        new SelectorWrapperArgument(
                            new NicknameArgument(Text.of(player), plugin.getUserDataManager(), NicknameArgument.UnderlyingType.PLAYER),
                            permissions,
                            SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS),
                        permissions.getPermissionWithSuffix("others")))),
                GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.bool(Text.of(toggle))))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = this.getUserFromArgs(Player.class, src, player, args);
        UserService uc = udm.get(pl).get();
        boolean fly = args.<Boolean>getOne(toggle).orElse(!pl.get(Keys.CAN_FLY).orElse(false));

        if (!setFlying(pl, fly)) {
            src.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat("command.fly.error"));
            return CommandResult.empty();
        }

        uc.setFlying(fly);
        if (pl != src) {
            src.sendMessages(plugin.getMessageProvider().getTextMessageWithFormat(fly ? "command.fly.player.on" : "command.fly.player.off", pl.getName()));
        }

        pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat(fly ? "command.fly.on" : "command.fly.off"));
        return CommandResult.success();
    }

    private boolean setFlying(Player pl, boolean fly) {
        // Only if we don't want to fly, offer IS_FLYING as false.
        return !(!fly && !pl.offer(Keys.IS_FLYING, false).isSuccessful()) && pl.offer(Keys.CAN_FLY, fly).isSuccessful();
    }
}
