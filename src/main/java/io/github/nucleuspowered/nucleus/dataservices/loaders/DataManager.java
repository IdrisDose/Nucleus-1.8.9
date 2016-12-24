/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices.loaders;

import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.dataservices.Service;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class DataManager<I, P, S extends Service> {

    final Function<I, DataProvider<P>> dataProviderFactory;
    final Map<I, S> dataStore = new ConcurrentHashMap<>();
    final NucleusPlugin plugin;

    DataManager(NucleusPlugin plugin, Function<I, DataProvider<P>> dataProviderFactory) {
        this.dataProviderFactory = dataProviderFactory;
        this.plugin = plugin;
    }

    public abstract Optional<S> get(I data);

    public final void saveAll() {
        dataStore.forEach((i, s) -> {
            if (!s.save()) {
                plugin.getLogger().error("Could not save data for " + i.toString());
            }
        });
    }
}
