/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.data.NucleusWorld;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * A service that retrieves {@link NucleusWorld}s.
 */
public interface NucleusWorldLoaderService {

    /**
     * Gets the world associated with the provided UUID.
     * @param uuid The {@link UUID} of the world to load.
     * @return The {@link NucleusWorld} that contains the Essenence data for the world.
     */
    Optional<NucleusWorld> getWorld(UUID uuid);

    /**
     * Gets the world associated with the provided UUID.
     * @param world The {@link World} to load.
     * @return The {@link NucleusWorld} that contains the Essenence data for the world.
     *
     * @throws IOException If the data file could not be read
     * @throws ObjectMappingException If the data file is malformed.
     * @throws Exception For any other reason
     */
    Optional<NucleusWorld> getWorld(World world) throws Exception;

    /**
     * Saves all world data.
     */
    void saveAll();
}
