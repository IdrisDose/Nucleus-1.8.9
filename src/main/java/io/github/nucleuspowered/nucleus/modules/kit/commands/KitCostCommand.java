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
 * Sets kit cost.
 *
 * Command Usage: /kit cost [kit] [cost] Permission: plugin.kit.cost.base
 */
@Permissions(prefix = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"cost", "setcost"}, subcommandOf = KitCommand.class)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitCostCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitHandler kitConfig;
    @Inject private KitConfigAdapter kca;

    private final String costKey = "cost";
    private final String kitKey = "kit";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(new KitArgument(Text.of(kitKey), kca, kitConfig, false)),
                GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of(costKey)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        KitArgument.KitInfo kitInfo = args.<KitArgument.KitInfo>getOne(kitKey).get();
        double cost = args.<Double>getOne(costKey).get();

        if (cost < 0) {
            cost = 0;
        }

        kitInfo.kit.setCost(cost);
        kitConfig.saveKit(kitInfo.name, kitInfo.kit);
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.kit.cost.success", kitInfo.name, String.valueOf(cost)));
        return CommandResult.success();
    }
}
