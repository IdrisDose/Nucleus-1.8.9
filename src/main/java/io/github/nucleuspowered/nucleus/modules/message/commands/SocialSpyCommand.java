/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Permissions(suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand("socialspy")
public class SocialSpyCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String arg = "Social Spy";
    @Inject private UserDataManager userConfigLoader;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.bool(Text.of(arg))))};
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        UserService qs = userConfigLoader.get(src).get();
        boolean spy = args.<Boolean>getOne(arg).orElse(!qs.isSocialSpy());
        if (qs.setSocialSpy(spy)) {
            Text message = plugin.getMessageProvider().getTextMessageWithFormat(spy ? "command.socialspy.on" : "command.socialspy.off");
            src.sendMessage(message);
            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.socialspy.unable"));
        return CommandResult.empty();
    }
}
