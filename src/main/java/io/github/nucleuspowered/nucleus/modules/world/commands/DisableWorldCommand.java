/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.NucleusWorldPropertiesArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
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
@Permissions(prefix = "world", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = { "disable", "dis" }, subcommandOf = WorldCommand.class)
public class DisableWorldCommand extends AbstractCommand<CommandSource> {

    private final String worldKey = "world";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.onlyOne(new NucleusWorldPropertiesArgument(Text.of(worldKey), NucleusWorldPropertiesArgument.Type.ENABLED_ONLY))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        WorldProperties worldProperties = args.<WorldProperties>getOne(worldKey).get();
        if (!worldProperties.isEnabled()) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.world.disable.alreadydisabled", worldProperties.getWorldName()));
        }

        if (Sponge.getServer().getWorld(worldProperties.getUniqueId()).isPresent()) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.world.disable.warnloaded", worldProperties.getWorldName()));
        }

        worldProperties.setEnabled(false);
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.disable.success", worldProperties.getWorldName()));
        return CommandResult.success();
    }
}
