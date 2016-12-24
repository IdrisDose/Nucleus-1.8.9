/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import org.spongepowered.api.entity.living.player.User;

/**
 * A service that contains message related APIs.
 */
public interface NucleusPrivateMessagingService {

    /**
     * Returns whether the user is able to see all private messages sent on the server. This indicates that the user
     * has the correct permission AND has activated it.
     *
     * @param user The {@link User} to check.
     * @return <code>true</code> if the user has Social Spy enabled.
     */
    boolean isSocialSpy(User user);

    /**
     * Sets whether the user is able to see all private messages on the server. This method will return whether the
     * system has fulfilled the request.
     *
     * @param user The {@link User}
     * @param isSocialSpy <code>true</code> to turn Social Spy on, <code>false</code> otherwise.
     * @return <code>true</code> if the change was fulfilled, <code>false</code> if the user does not have permission
     */
    boolean setSocialSpy(User user, boolean isSocialSpy);
}
