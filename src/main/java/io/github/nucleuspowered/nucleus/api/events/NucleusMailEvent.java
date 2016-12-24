/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

/**
 * An event that is posted when a player uses /mail in NucleusPlugin
 */
public interface NucleusMailEvent extends Event, Cancellable {
    /**
     * The sender of the mail.
     *
     * @return The sender
     */
    User getSender();

    /**
     * The recipient of the mail.
     *
     * @return The recipient.
     */
    User getRecipient();

    /**
     * The message that was sent.
     *
     * @return The message.
     */
    String getMessage();
}
