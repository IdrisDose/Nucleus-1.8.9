/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes;

import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

@ConfigSerializable
public class GeneralDataNode {

    @Setting
    private Map<String, LocationNode> jails = Maps.newHashMap();

    @Setting
    private Map<String, WarpNode> warps = Maps.newHashMap();

    @Setting
    @Nullable
    private LocationNode firstspawn = null;

    public Map<String, LocationNode> getJails() {
        return jails;
    }

    public Map<String, WarpNode> getWarps() {
        return warps;
    }

    public Optional<LocationNode> getFirstSpawnLocation() {
        return Optional.ofNullable(firstspawn);
    }

    public void setFirstSpawnLocation(@Nullable LocationNode node) {
        firstspawn = node;
    }

}
