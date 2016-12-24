/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

@NoWarmup
@NoCooldown
@NoCost
@Permissions(prefix = "world.gamerule", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = { "set" }, subcommandOf = GameruleCommand.class)
public class SetGameruleCommand extends AbstractCommand<CommandSource> {

    private static String worldKey = "world";
    private static String gameRuleKey = "gamerule";
    private static String valueKey = "value";

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.world(Text.of(worldKey)))),
            GenericArguments.string(Text.of(gameRuleKey)),
            GenericArguments.string(Text.of(valueKey))
        };
    }

    @Override public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        WorldProperties worldProperties = getWorldFromUserOrArgs(src, worldKey, args);
        String gameRule = args.<String>getOne(gameRuleKey).get();
        String value = args.<String>getOne(valueKey).get();

        worldProperties.setGameRule(gameRule, value);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.gamerule.set.success", gameRule, value, worldProperties.getWorldName()));
        return CommandResult.success();
    }
}
