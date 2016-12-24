/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.runnables;

import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.internal.TaskBase;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfig;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.commandlogger.handlers.CommandLoggerHandler;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class CommandLoggerRunnable extends TaskBase {

    @Inject private CommandLoggerConfigAdapter clca;
    @Inject private CommandLoggerHandler handler;
    private CommandLoggerConfig config = null;

    @Inject
    public CommandLoggerRunnable(NucleusPlugin plugin) {
        plugin.registerReloadable(() -> config = null);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public TimePerRun interval() {
        return new TimePerRun(1, TimeUnit.SECONDS);
    }

    @Override
    public void accept(Task task) {
        if (Sponge.getGame().getState() == GameState.SERVER_STOPPED) {
            return;
        }

        if (config == null) {
            config = clca.getNodeOrDefault();
        }

        if (config.isLogToFile()) {
            handler.onTick();
        }
    }
}
