/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.events;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class NucleusReloadConfigEvent extends AbstractEvent {

    private final Cause cause;

    public NucleusReloadConfigEvent(NucleusPlugin plugin) {
        Preconditions.checkNotNull(plugin);
        cause = Cause.source(plugin).build();
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
