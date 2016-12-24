/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.events;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class InternalNucleusHelpOpEvent extends AbstractEvent implements Cancellable {

    private final CommandSource from;
    private final String message;
    private boolean isCancelled = false;

    public InternalNucleusHelpOpEvent(CommandSource from, String message) {
        this.from = from;
        this.message = message;
    }

    @Override
    public Cause getCause() {
        return Cause.of(NamedCause.source(from));
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
