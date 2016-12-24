/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import io.github.nucleuspowered.nucleus.api.data.seen.SeenInformationProvider;

import javax.annotation.Nonnull;

/**
 * This service allows plugins to register handlers that will display information when a player runs the /seen command.
 *
 * <p>
 *     Plugins are expected to only register <strong>one</strong> {@link SeenInformationProvider}.
 * </p>
 *
 * <p>
 *     Consumers of this API should also note that this will run <strong>asynchronously</strong>. No methods that would
 *     require the use of a synchronous API should be used here.
 * </p>
 */
public interface NucleusSeenService {

    /**
     * Registers a {@link SeenInformationProvider} with NucleusPlugin.
     *
     * @param plugin The plugin registering the service.
     * @param seenInformationProvider The {@link SeenInformationProvider}
     * @throws IllegalArgumentException Thrown if the plugin has either
     * <ul>
     *  <li>Registered a {@link SeenInformationProvider} already</li>
     *  <li>Not provided the {@link org.spongepowered.api.plugin.Plugin} annotated class</li>
     * </ul>
     */
    void register(@Nonnull Object plugin, @Nonnull SeenInformationProvider seenInformationProvider) throws IllegalArgumentException;
}
