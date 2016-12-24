/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

@ConfigSerializable
public class WarpNode extends LocationNode {

    @Setting("cost")
    private int cost = -1;

    @Setting("category")
    private String category = null;

    public WarpNode() {
        super();
    }

    public WarpNode(Location<World> location, Vector3d rotation) {
        this(location, rotation, -1);
    }

    public WarpNode(Location<World> location, Vector3d rotation, int cost) {
        super(location, rotation);
        this.cost = cost;
    }

    public WarpNode(Location<World> location) {
        this(location, -1);
    }

    public WarpNode(Location<World> location, int cost) {
        super(location);
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        if (cost < -1) {
            this.cost = -1;
        }

        this.cost = cost;
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }
}
