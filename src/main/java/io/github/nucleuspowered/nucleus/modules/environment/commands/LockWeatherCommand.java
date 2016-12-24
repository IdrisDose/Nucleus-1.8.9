/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.data.NucleusWorld;
import io.github.nucleuspowered.nucleus.dataservices.loaders.WorldDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

/**
 * Locks (or unlocks) the weather.
 *
 * Permission: plugin.lockweather.base
 */
@Permissions
@RunAsync
@RegisterCommand({ "lockweather", "killweather" })
@NoWarmup
@NoCooldown
@NoCost
public class LockWeatherCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private WorldDataManager loader;

    private final String worldKey = "world";
    private final String toggleKey = "toggle";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.onlyOne(GenericArguments.optionalWeak(GenericArguments.world(Text.of(worldKey)))),
                GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.bool(Text.of(toggleKey))))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<WorldProperties> world = getWorldProperties(src, worldKey, args);
        if (!world.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.specifyworld"));
            return CommandResult.empty();
        }

        WorldProperties wp = world.get();
        Optional<NucleusWorld> ws = loader.getWorld(wp.getUniqueId());
        if (!ws.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.noworld", wp.getWorldName()));
            return CommandResult.empty();
        }

        boolean toggle = args.<Boolean>getOne(toggleKey).orElse(!ws.get().isLockWeather());

        ws.get().setLockWeather(toggle);
        if (toggle) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.lockweather.locked", wp.getWorldName()));
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.lockweather.unlocked", wp.getWorldName()));
        }

        return CommandResult.success();
    }
}
