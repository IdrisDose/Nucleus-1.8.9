/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.events;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class InternalNucleusMessageEvent extends AbstractEvent implements io.github.nucleuspowered.nucleus.api.events.NucleusMessageEvent {

    private final CommandSource from;
    private final CommandSource to;
    private final String message;
    private boolean isCancelled = false;

    public InternalNucleusMessageEvent(CommandSource from, CommandSource to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public Cause getCause() {
        return Cause.of(NamedCause.source(from));
    }

    @Override
    public CommandSource getSender() {
        return from;
    }

    @Override
    public CommandSource getRecipient() {
        return to;
    }

    @Override
    public String getMessage() {
        return message;
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
