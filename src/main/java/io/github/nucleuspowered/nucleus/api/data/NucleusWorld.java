/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.NucleusPlugin;

import java.util.Optional;

/**
 * Represents data held about a world in {@link NucleusPlugin}.
 */
public interface NucleusWorld {

    /**
     * Gets whether the weather has been locked in this world.
     *
     * @return <code>true</code> if the weather has been locked.
     */
    boolean isLockWeather();

    /**
     * Sets whether the weather has been locked in this world.
     *
     * @param lockWeather <code>true</code> if the weather should be locked, <code>false</code> otherwise.
     */
    void setLockWeather(boolean lockWeather);

    Optional<Vector3d> getSpawnRotation();

    void setSpawnRotation(Vector3d rotation);

    void clearSpawnRotation();
}
