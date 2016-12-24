/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands.exp;

import io.github.nucleuspowered.nucleus.argumentparsers.ExperienceLevelArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.modules.admin.commands.ExperienceCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@NoCooldown
@NoWarmup
@NoCost
@Permissions(prefix = "exp")
@RegisterCommand(value = "set", subcommandOf = ExperienceCommand.class)
public class SetExperience extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String levelKey = "level";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.player(Text.of(ExperienceCommand.playerKey)))),
                GenericArguments.firstParsing(
                        GenericArguments.onlyOne(new ExperienceLevelArgument(Text.of(levelKey))),
                        GenericArguments.onlyOne(new PositiveIntegerArgument(Text.of(ExperienceCommand.experienceKey)))
                )
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = this.getUserFromArgs(Player.class, src, ExperienceCommand.playerKey, args);
        if (!ExperienceCommand.checkGameMode(pl, src)) {
            return CommandResult.empty();
        }

        Optional<Integer> l = args.getOne(levelKey);
        DataTransactionResult dtr;
        if (l.isPresent()) {
            dtr = pl.offer(Keys.EXPERIENCE_LEVEL, l.get());
        } else {
            dtr = pl.offer(Keys.TOTAL_EXPERIENCE, args.<Integer>getOne(ExperienceCommand.experienceKey).get());
        }

        return ExperienceCommand.tellUserAboutExperience(src, pl, dtr.isSuccessful());
    }
}
