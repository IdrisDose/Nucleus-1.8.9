/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.KitArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

/**
 * Sets kit as a one time use.
 *
 * Command Usage: /kit set Permission: plugin.kit.onetime.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"onetime"}, subcommandOf = KitCommand.class)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitOneTimeCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitHandler kitConfig;
    @Inject private KitConfigAdapter kca;

    private final String kit = "kit";
    private final String toggle = "oneTimeToggle";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.seq(GenericArguments.onlyOne(new KitArgument(Text.of(kit), kca, kitConfig, true)),
            GenericArguments.onlyOne(GenericArguments.bool(Text.of(toggle))))
        };
    }

    @Override
    public CommandResult executeCommand(final CommandSource player, CommandContext args) throws Exception {
        KitArgument.KitInfo kitInfo = args.<KitArgument.KitInfo>getOne(kit).get();
        boolean b = args.<Boolean>getOne(toggle).get();

        // This Kit is a reference back to the version in list, so we don't need
        // to update it explicitly
        kitInfo.kit.setOneTime(b);
        kitConfig.saveKit(kitInfo.name, kitInfo.kit);
        player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat(b ? "command.kit.onetime.on" : "command.kit.onetime.off", kitInfo.name));

        return CommandResult.success();
    }
}
