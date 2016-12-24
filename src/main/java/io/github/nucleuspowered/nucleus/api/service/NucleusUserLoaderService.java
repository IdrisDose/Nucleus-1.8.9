/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.data.NucleusUser;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A service that retrieves {@link NucleusUser}s.
 */
public interface NucleusUserLoaderService {

    /**
     * Gets a list of {@link NucleusUser}s that represents the online.
     *
     * @return A {@link List} of {@link NucleusUser}s.
     */
    List<NucleusUser> getOnlineUsers();

    /**
     * Gets the user data file from their UUID.
     *
     * <p>
     *     Consumers of this API should note that this method handles some caching of {@link User} objects, and in most
     *     cases, will load {@link User} objects from memory, and will handle holding the object in memory after loading
     *     from the persistent data source. Consumers are therefore requested to NOT hold onto user objects in their
     *     code, preferring the UUID of the user so that an up to date object is returned should there be a requirement
     *     to reload the user from disc.
     * </p>
     *
     * @param playerUUID The {@link UUID} of the player in question.
     * @return The {@link NucleusUser}, wrapped in an {@link Optional}
     */
    Optional<NucleusUser> getUser(UUID playerUUID);

    /**
     * Gets the user data file from the {@link User}.
     *
     * <p>
     *     Consumers of this API should note that this method handles some caching of {@link User} objects, and in most
     *     cases, will load {@link User} objects from memory, and will handle holding the object in memory after loading
     *     from the persistent data source. Consumers are therefore requested to NOT hold onto user objects in their
     *     code, preferring the UUID of the user so that an up to date object is returned should there be a requirement
     *     to reload the user from disc.
     * </p>
     *
     * @param user The {@link User} of the player in question.
     * @return The {@link NucleusUser}
     */
    Optional<NucleusUser> getUser(User user);
}
