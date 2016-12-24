/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

@RegisterCommand({"exp", "experience", "xp"})
@Permissions
@NoCooldown
@NoWarmup
@NoCost
public class ExperienceCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    public static final String playerKey = "player";
    public static final String experienceKey = "experience";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] { GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of(playerKey))) };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = args.<Player>getOne(playerKey).get();

        ExperienceHolderData ehd = pl.get(ExperienceHolderData.class).get();
        int exp = ehd.totalExperience().get();
        int lv = ehd.level().get();

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.exp.info", pl.getName(), String.valueOf(exp), String.valueOf(lv)));
        return CommandResult.success();
    }

    public static CommandResult tellUserAboutExperience(CommandSource src, Player pl, boolean isSuccess) {
        if (!isSuccess) {
            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.exp.set.error"));
            return CommandResult.empty();
        }

        int exp = pl.get(Keys.TOTAL_EXPERIENCE).get();
        int newLvl = pl.get(Keys.EXPERIENCE_LEVEL).get();

        if (!src.equals(pl)) {
            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.exp.set.new.other", pl.getName(), String.valueOf(exp), String.valueOf(newLvl)));
        }

        pl.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.exp.set.new.self", String.valueOf(exp), String.valueOf(newLvl)));
        return CommandResult.success();
    }

    public static boolean checkGameMode(Player pl, CommandSource src) {
        GameMode gm = pl.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL);
        if (gm == GameModes.CREATIVE || gm == GameModes.SPECTATOR) {
            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.exp.gamemode", pl.getName()));
            return false;
        }

        return true;
    }
}
