/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.events;

import io.github.nucleuspowered.nucleus.api.events.NucleusChangeNicknameEvent;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;

public class ChangeNicknameEvent extends AbstractEvent implements NucleusChangeNicknameEvent {

    private final Cause cause;
    private final Text newNickname;
    private final User target;
    private boolean cancel = false;

    public ChangeNicknameEvent(Cause cause, Text newNickname, User target) {
        this.cause = cause;
        this.newNickname = newNickname;
        this.target = target;
    }

    @Override
    public Text getNewNickname() {
        return newNickname;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public User getTargetUser() {
        return target;
    }
}
