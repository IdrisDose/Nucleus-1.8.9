/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.data.WarnData;
import io.github.nucleuspowered.nucleus.argumentparsers.WarningArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.warn.handlers.WarnHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.List;

@Permissions(suggestedLevel = SuggestedLevel.ADMIN)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"removewarning", "deletewarning", "delwarn"})
public class RemoveWarningCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject
    private WarnHandler handler;
    private final String warningKey = "warning";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.flags().flag("-remove", "r").buildWith(
                GenericArguments.onlyOne(new WarningArgument(Text.of(warningKey), plugin, handler)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        WarningArgument.Result result = args.<WarningArgument.Result>getOne(warningKey).get();
        User user = result.user;
        boolean removePermanently = false;
        if (args.hasAny("remove")) {
            removePermanently = true;
        }

        List<WarnData> warnings = handler.getWarnings(user);
        if (warnings.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.none", user.getName()));
            return CommandResult.success();
        }

        if (handler.removeWarning(user, result.warnData, removePermanently)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.removewarning.success", user.getName()));
            return CommandResult.success();
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.removewarning.failure", user.getName()));
        return CommandResult.empty();
    }
}
