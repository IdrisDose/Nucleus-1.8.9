/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.data.MuteData;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

/**
 * A service that determines whether a player is muted.
 */
public interface NucleusMuteService {

    /**
     * Gets whether a player is muted.
     *
     * @param user The {@link User} to check.
     * @return Whether the player is muted.
     */
    boolean isMuted(User user);

    /**
     * Gets hte {@link MuteData} of a player if they are muted.
     *
     * @param user The {@link User} to check.
     * @return An {@link Optional} containing the mute if they are muted.
     */
    Optional<MuteData> getPlayerMuteData(User user);

    /**
     * Mutes a player for a specified duration.
     *
     * @param user The {@link User} to mute.
     * @param muteData The {@link MuteData}.
     * @return <code>true</code> if they were muted. <code>false</code> if they are already muted.
     */
    boolean mutePlayer(User user, MuteData muteData);

    /**
     * Unmutes a player.
     *
     * @param user The {@link User} to unmute
     * @return <code>true</code> if they were unmuted.
     */
    boolean unmutePlayer(User user);
}
