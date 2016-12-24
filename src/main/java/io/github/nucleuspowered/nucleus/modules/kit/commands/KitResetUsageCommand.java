/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.KitArgument;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

/**
 * Resets a kit usage for player.
 *
 * Command Usage: /kit list Permission: plugin.kit.list.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"resetusage", "reset"}, subcommandOf = KitCommand.class)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitResetUsageCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitHandler kitConfig;
    @Inject private KitConfigAdapter kca;
    @Inject private UserDataManager userConfigLoader;

    private final String kit = "kit";
    private final String user = "player";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.onlyOne(GenericArguments.user(Text.of(user))),
            GenericArguments.onlyOne(new KitArgument(Text.of(kit), kca, kitConfig, false))
        };
    }

    @Override
    public CommandResult executeCommand(final CommandSource player, CommandContext args) throws Exception {
        KitArgument.KitInfo kitInfo = args.<KitArgument.KitInfo>getOne(kit).get();
        User u = args.<User>getOne(user).get();
        UserService inu = userConfigLoader.get(u).get();

        if (inu.getKitLastUsedTime().containsKey(kitInfo.name.toLowerCase())) {
            // Remove the key.
            inu.removeKitLastUsedTime(kitInfo.name.toLowerCase());

            player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.resetuser.success", u.getName(), kitInfo.name));
            return CommandResult.success();
        }

        player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.resetuser.empty", u.getName(), kitInfo.name));
        return CommandResult.empty();
    }
}
