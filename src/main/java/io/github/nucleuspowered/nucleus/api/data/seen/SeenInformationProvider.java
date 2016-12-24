/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data.seen;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A {@link SeenInformationProvider} object can hook into the {@code seen} command and provide extra information on a player.
 *
 * <p>
 *     This must be registered with the {@link io.github.nucleuspowered.nucleus.api.service.NucleusSeenService}
 * </p>
 */
public interface SeenInformationProvider {

    /**
     * Gets whether the requesting {@link CommandSource} has permission to request the provided information for the
     * requested {@link User}.
     *
     * @param source The {@link CommandSource} who ran the {@code seen} command.
     * @param user The {@link User} that information has been requested about.
     * @return {@code true} if the command should show the user this information.
     */
    boolean hasPermission(@Nonnull CommandSource source, @Nonnull User user);

    /**
     * Gets the information to display to the {@link CommandSource} about the {@link User}
     *
     * <p>
     *     Permission checks should NOT be done here, but in the {@link #hasPermission(CommandSource, User)} check.
     * </p>
     *
     * @param source The {@link CommandSource} who ran the {@code seen} command.
     * @param user The {@link User} that information has been requested about.
     * @return The {@link Collection} containing the {@link Text} to display to the user, or an empty iterable. It is
     *         recommended, for obvious reasons, that this is ordered! May return {@code null} if there is nothing
     *         to return.
     */
    Collection<Text> getInformation(@Nonnull CommandSource source, @Nonnull User user);
}
