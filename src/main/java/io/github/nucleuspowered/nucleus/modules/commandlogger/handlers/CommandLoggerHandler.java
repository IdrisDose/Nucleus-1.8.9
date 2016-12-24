/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.handlers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.logging.DateRotatableFileLogger;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfigAdapter;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class CommandLoggerHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
    private final CommandLoggerConfigAdapter clca;
    private final CoreConfigAdapter coreConfigAdapter;
    private DateRotatableFileLogger logger;
    private final List<String> queueEntry = Lists.newArrayList();
    private final NucleusPlugin plugin;
    private final Object locking = new Object();

    @Inject
    public CommandLoggerHandler(NucleusPlugin plugin, CommandLoggerConfigAdapter clca, CoreConfigAdapter coreConfigAdapter) {
        this.clca = clca;
        this.plugin = plugin;
        this.coreConfigAdapter = coreConfigAdapter;
    }

    public void queueEntry(String s) {
        if (logger != null) {
            synchronized (locking) {
                queueEntry.add(s);
            }
        }
    }

    public void onReload() throws Exception {
        if (clca.getNodeOrDefault().isLogToFile() && logger == null) {
            this.createLogger();
        } else if (!clca.getNodeOrDefault().isLogToFile() && logger != null) {
            onShutdown();
        }
    }

    public void onServerShutdown() throws IOException {
        Preconditions.checkState(Sponge.getGame().getState().equals(GameState.SERVER_STOPPED));
        onShutdown();
    }

    private void onShutdown() throws IOException {
        if (logger != null) {
            logger.close();
            logger = null;
        }
    }

    public void onTick() {
        if (queueEntry.isEmpty()) {
            return;
        }

        List<String> l;
        synchronized (locking) {
            l = Lists.newArrayList(queueEntry);
            queueEntry.clear();
        }

        if (logger == null) {
            if (clca.getNodeOrDefault().isLogToFile()) {
                try {
                    createLogger();
                } catch (IOException e) {
                    plugin.getLogger().warn(NucleusPlugin.getNucleus().getMessageProvider().getMessageWithFormat("commandlog.couldnotwrite"));
                    if (coreConfigAdapter.getNodeOrDefault().isDebugmode()) {
                        e.printStackTrace();
                    }

                    return;
                }
            } else {
                return;
            }
        }

        try {
            writeEntry(l);
        } catch (IOException e) {
            plugin.getLogger().warn(NucleusPlugin.getNucleus().getMessageProvider().getMessageWithFormat("commandlog.couldnotwrite"));
            if (coreConfigAdapter.getNodeOrDefault().isDebugmode()) {
                e.printStackTrace();
            }
        }
    }

    private void createLogger() throws IOException {
        logger = new DateRotatableFileLogger("command", "cmds", s -> "[" +
                formatter.format(Instant.now().atZone(ZoneOffset.systemDefault())) +
                "] " + s);
    }

    private void writeEntry(Iterable<String> entry) throws IOException {
        if (logger != null) {
            logger.logEntry(entry);
        }
    }
}
