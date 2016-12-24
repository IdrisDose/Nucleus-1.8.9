/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.qsml.module;

import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;

import java.util.Optional;

public abstract class ConfigurableModule<A extends NucleusConfigAdapter<?>> extends StandardModule {

    private A adapter;

    /**
     * Gets a new instance of the unattached config adapter.
     *
     * @return The adapter.
     */
    public abstract A getAdapter();

    @Override
    public final Optional<AbstractConfigAdapter<?>> getConfigAdapter() {
        if (adapter == null) {
            adapter = getAdapter();
            plugin.getInjector().injectMembers(adapter);
        }

        // We need to use the right type...
        return Optional.of(adapter);
    }
}
