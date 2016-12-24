/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import io.github.nucleuspowered.nucleus.argumentparsers.NucleusWorldPropertiesArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.modules.world.WorldHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.LocatedSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

import javax.inject.Inject;

@Permissions(prefix = "world.border")
@RegisterCommand(value = {"gen", "genchunks", "generatechunks", "chunkgen"}, subcommandOf = BorderCommand.class)
public class GenerateChunksCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String worldKey = "world";

    @Inject
    private WorldHelper worldHelper;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optional(GenericArguments.onlyOne(new NucleusWorldPropertiesArgument(Text.of(worldKey), NucleusWorldPropertiesArgument.Type.ENABLED_ONLY)))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        WorldProperties wp = getWorldFromUserOrArgs(src, worldKey, args);
        if (worldHelper.isPregenRunningForWorld(wp.getUniqueId())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.gen.alreadyrunning", wp.getWorldName()));
            return CommandResult.empty();
        }

        Optional<World> w = Sponge.getServer().getWorld(wp.getUniqueId());
        if (!w.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.gen.notloaded", wp.getWorldName()));
            return CommandResult.empty();
        }

        // Create the task.
        worldHelper.startPregenningForWorld(w.get());
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.world.gen.started", wp.getWorldName()));

        return CommandResult.success();
    }
}
