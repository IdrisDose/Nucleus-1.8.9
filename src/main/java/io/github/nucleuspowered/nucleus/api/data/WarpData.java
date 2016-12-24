/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

public class WarpData extends LocationData {

    private final int cost;
    private final String category;

    public WarpData(String name, Location<World> location, Vector3d rotation, int cost, @Nullable String category) {
        super(name, location, rotation);
        this.cost = Math.max(-1, cost);
        this.category = category;
    }

    public Optional<Integer> getCost() {
        if (cost > -1) {
            return Optional.of(cost);
        }

        return Optional.empty();
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }

    @Override
    public String toString() {
        return super.toString() + ", cost: " + cost;
    }
}
