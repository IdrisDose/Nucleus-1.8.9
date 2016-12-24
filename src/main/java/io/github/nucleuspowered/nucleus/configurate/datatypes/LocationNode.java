/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.exceptions.NoSuchWorldException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * We use this class in order to not just accidentally delete data when a world isn't available.
 */
@ConfigSerializable
public class LocationNode {

    @Setting
    double x;

    @Setting
    double y;

    @Setting
    double z;

    @Setting
    double rotx;

    @Setting
    double roty;

    @Setting
    double rotz;

    @Setting
    UUID world;

    public LocationNode() { }

    public LocationNode(Location<World> length) {
        this(length, new Vector3d());
    }

    public LocationNode(Location<World> length, Vector3d rotation) {
        this.x = length.getX();
        this.y = length.getY();
        this.z = length.getZ();
        this.rotx = rotation.getX();
        this.roty = rotation.getY();
        this.rotz = rotation.getZ();
        this.world = length.getExtent().getUniqueId();
    }

    /**
     * Gets a {@link Location} from the node.
     *
     * @return The Location
     * @throws NoSuchWorldException The world does not exist.
     */
    public Location<World> getLocation() throws NoSuchWorldException {
        Optional<World> ow = Sponge.getServer().getWorld(world);

        if (ow.isPresent()) {
            return new Location<>(ow.get(), x, y, z);
        }

        throw new NoSuchWorldException();
    }

    public Vector3d getRotation() {
        return new Vector3d(rotx, roty, rotz);
    }
}
