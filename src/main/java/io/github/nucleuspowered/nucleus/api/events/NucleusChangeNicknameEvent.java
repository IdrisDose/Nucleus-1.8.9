/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.text.Text;

/**
 * Fired when a player requests a nickname.
 */
public interface NucleusChangeNicknameEvent extends Cancellable, TargetUserEvent {

    /**
     * The new nickname for the {@link #getTargetUser()}
     *
     * @return The nickname.
     */
    Text getNewNickname();
}
