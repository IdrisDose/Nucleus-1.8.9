/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.data.WarpData;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Gets a service that allows users to warp about using defined warp.
 */
public interface NucleusWarpService {

    /**
     * Gets the location for the specified warp.
     *
     * @param warpName The name of the warp to check.
     * @return The {@link Location} of the warp, or {@link Optional#empty()} otherwise.
     */
    Optional<WarpData> getWarp(String warpName);

    /**
     * Removes a warp.
     *
     * @param warpName The name of the warp.
     * @return <code>true</code> if the warp was there and removed, <code>false</code> if the warp never existed.
     */
    boolean removeWarp(String warpName);

    /**
     * Sets a warp, will not overwrite current warp.
     *
     * @param warpName The name of the warp to set.
     * @param location The location of the warp.
     * @param rotation The rotation of the warp.
     * @return <code>true</code> if set, <code>false</code> otherwise.
     */
    boolean setWarp(String warpName, Location<World> location, Vector3d rotation);

    List<WarpData> getUncategorisedWarps();

    List<WarpData> getWarpsForCategory(String category);

    Map<String, List<WarpData>> getCategorisedWarps();

    Map<String, List<WarpData>> getCategorisedWarps(Predicate<WarpData> warpDataPredicate);

    boolean removeWarpCost(String warpName);

    /**
     * Sets the cost of a warp.
     *
     * @param warpName The name of the warp to change the cost of.
     * @param cost The cost to use the warp. Set to zero to disable.
     * @return <code>true</code> if the cost is set, <code>false</code> otherwise.
     */
    boolean setWarpCost(String warpName, int cost);

    boolean setWarpCategory(String warpName, @Nullable String category);

    /**
     * Gets the names of all the warp that are available.
     *
     * @return A set of warp.
     */
    Set<String> getWarpNames();

    /**
     * Gets whether a warp exists.
     *
     * @param name The name to check for.
     * @return <code>true</code> if it exists, <code>false</code> otherwise.
     */
    boolean warpExists(String name);
}
