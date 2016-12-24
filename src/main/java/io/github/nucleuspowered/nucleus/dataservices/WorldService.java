/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.api.data.NucleusWorld;
import io.github.nucleuspowered.nucleus.configurate.datatypes.WorldDataNode;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.Vector3dTypeSerialiser;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldService extends Service<WorldDataNode> implements NucleusWorld {

    private static final Map<TypeToken<?>, TypeSerializer<?>> serializerMap;

    static {
        serializerMap = Maps.newHashMap();
        serializerMap.put(TypeToken.of(Vector3d.class), new Vector3dTypeSerialiser());
    }

    private final NucleusPlugin plugin;

    private final UUID world;

    public WorldService(NucleusPlugin plugin, DataProvider<WorldDataNode> provider, World world) throws Exception {
        super(provider);
        Preconditions.checkNotNull("world", world);
        Preconditions.checkNotNull("plugin", plugin);
        this.plugin = plugin;
        this.world = world.getUniqueId();
    }

    public UUID getUniqueID() {
        return world;
    }

    @Override
    public boolean isLockWeather() {
        return data.isLockWeather();
    }

    @Override
    public void setLockWeather(boolean lockWeather) {
        data.setLockWeather(lockWeather);
    }

    @Override
    public Optional<Vector3d> getSpawnRotation() {
        return data.getSpawnRotation();
    }

    @Override
    public void setSpawnRotation(Vector3d rotation) {
        data.setSpawnRotation(rotation);
    }

    @Override
    public void clearSpawnRotation() {
        data.setSpawnRotation(null);
    }
}
