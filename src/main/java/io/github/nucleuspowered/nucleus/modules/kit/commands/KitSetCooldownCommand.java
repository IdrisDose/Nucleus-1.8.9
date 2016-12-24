/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.argumentparsers.KitArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.TimespanArgument;
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

import java.time.Duration;

/**
 * Sets kit cooldown.
 *
 * Command Usage: /kit set Permission: plugin.kit.set.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"setcooldown", "setinterval"}, subcommandOf = KitCommand.class)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitSetCooldownCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitHandler kitConfig;
    @Inject private KitConfigAdapter kca;

    private final String kit = "kit";
    private final String duration = "duration";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.seq(GenericArguments.onlyOne(new KitArgument(Text.of(kit), kca, kitConfig, false)),
                GenericArguments.onlyOne(new TimespanArgument(Text.of(duration))))};
    }

    @Override
    public CommandResult executeCommand(final CommandSource player, CommandContext args) throws Exception {
        KitArgument.KitInfo kitInfo = args.<KitArgument.KitInfo>getOne(kit).get();
        long seconds = args.<Long>getOne(duration).get();

        kitInfo.kit.setInterval(Duration.ofSeconds(seconds));
        kitConfig.saveKit(kitInfo.name, kitInfo.kit);
        player.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.setcooldown.success", kitInfo.name, Util.getTimeStringFromSeconds(seconds)));
        return CommandResult.success();
    }
}
