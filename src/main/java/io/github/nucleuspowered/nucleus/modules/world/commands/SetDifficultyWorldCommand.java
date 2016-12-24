/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.argumentparsers.DifficultyArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.NucleusWorldPropertiesArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

@NoWarmup
@NoCooldown
@NoCost
@Permissions(prefix = "world", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"setdifficulty", "difficulty"}, subcommandOf = WorldCommand.class)
public class SetDifficultyWorldCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String difficulty = "difficulty";
    private final String world = "world";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.onlyOne(new DifficultyArgument(Text.of(difficulty))),
                GenericArguments.optional(GenericArguments.onlyOne(new NucleusWorldPropertiesArgument(Text.of(world), NucleusWorldPropertiesArgument.Type.ALL)))
        };
    }

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        Difficulty difficultyInput = args.<Difficulty>getOne(difficulty).get();
        Optional<WorldProperties> optWorldProperties = args.getOne(world);

        if (optWorldProperties.isPresent()) {
            optWorldProperties.get().setDifficulty(difficultyInput);
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.setdifficulty.success",
                optWorldProperties.get().getWorldName(),
                Util.getTranslatableIfPresent(difficultyInput)));
        } else {
            if (src instanceof Player) {
                Player player = (Player) src;
                player.getWorld().getProperties().setDifficulty(difficultyInput);
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.setdifficulty.success",
                    optWorldProperties.get().getWorldName(),
                    Util.getTranslatableIfPresent(difficultyInput)));
            } else {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.player"));
                return CommandResult.empty();
            }
        }

        return CommandResult.success();
    }
}
