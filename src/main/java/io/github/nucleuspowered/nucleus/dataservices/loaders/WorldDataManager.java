/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices.loaders;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.api.data.NucleusWorld;
import io.github.nucleuspowered.nucleus.api.service.NucleusWorldLoaderService;
import io.github.nucleuspowered.nucleus.configurate.datatypes.WorldDataNode;
import io.github.nucleuspowered.nucleus.dataservices.WorldService;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class WorldDataManager extends DataManager<UUID, WorldDataNode, WorldService> implements NucleusWorldLoaderService {

    public WorldDataManager(NucleusPlugin plugin, Function<UUID, DataProvider<WorldDataNode>> dataProviderFactory) {
        super(plugin, dataProviderFactory);
    }

    @Override
    public Optional<WorldService> get(UUID data) {
        try {
            if (dataStore.containsKey(data)) {
                return Optional.of(dataStore.get(data));
            }

            Optional<World> oworld = Sponge.getServer().getWorld(data);
            if (oworld.isPresent()) {
                DataProvider<WorldDataNode> d = dataProviderFactory.apply(data);
                if (d != null) {
                    WorldService us = new WorldService(plugin, d, oworld.get());
                    dataStore.put(data, us);
                    return Optional.of(us);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<NucleusWorld> getWorld(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return Optional.ofNullable(get(uuid).orElse(null));
    }

    @Override
    public Optional<NucleusWorld> getWorld(World world) throws Exception {
        Preconditions.checkNotNull(world);
        return getWorld(world.getUniqueId());
    }
}
