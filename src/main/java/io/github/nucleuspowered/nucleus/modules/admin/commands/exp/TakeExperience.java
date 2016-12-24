/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands.exp;

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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@NoCooldown
@NoWarmup
@NoCost
@Permissions(prefix = "exp")
@RegisterCommand(value = "take", subcommandOf = ExperienceCommand.class)
public class TakeExperience extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.player(Text.of(ExperienceCommand.playerKey)))),
                GenericArguments.onlyOne(new PositiveIntegerArgument(Text.of(ExperienceCommand.experienceKey)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = this.getUserFromArgs(Player.class, src, ExperienceCommand.playerKey, args);
        if (!ExperienceCommand.checkGameMode(pl, src)) {
            return CommandResult.empty();
        }

        int exp = pl.get(Keys.TOTAL_EXPERIENCE).get();
        exp -= args.<Integer>getOne(ExperienceCommand.experienceKey).get();

        return ExperienceCommand.tellUserAboutExperience(src, pl, pl.offer(Keys.TOTAL_EXPERIENCE, exp).isSuccessful());
    }
}
