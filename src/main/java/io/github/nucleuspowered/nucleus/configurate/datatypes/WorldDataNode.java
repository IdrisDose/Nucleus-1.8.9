/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Contains the config entries for any World Config.
 */
@ConfigSerializable
public class WorldDataNode {
    @Setting("lock-weather")
    private boolean lockWeather = false;

    @Setting("spawn-rotation")
    @Nullable
    private Vector3d spawnRotation = null;

    public boolean isLockWeather() {
        return lockWeather;
    }

    public void setLockWeather(boolean lockWeather) {
        this.lockWeather = lockWeather;
    }

    public Optional<Vector3d> getSpawnRotation() {
        return Optional.ofNullable(spawnRotation);
    }

    public void setSpawnRotation(@Nullable Vector3d spawnRotation) {
        this.spawnRotation = spawnRotation;
    }
}
