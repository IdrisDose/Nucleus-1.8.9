/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.handlers;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.data.WarpData;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarpService;
import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class WarpHandler implements NucleusWarpService {

    @Inject private GeneralService store;

    @Override
    public Optional<WarpData> getWarp(String warpName) {
        return store.getWarpLocation(warpName);
    }

    @Override
    public boolean removeWarp(String warpName) {
        return store.removeWarp(warpName);
    }

    @Override
    public boolean setWarp(String warpName, Location<World> location, Vector3d rotation) {
        return store.addWarp(warpName, location, rotation);
    }

    @Override
    public List<WarpData> getUncategorisedWarps() {
        return getWarpsForCategory(x -> !x.getCategory().isPresent());
    }

    @Override
    public List<WarpData> getWarpsForCategory(String category) {
        return getWarpsForCategory(x -> x.getCategory().isPresent() && x.getCategory().get().equalsIgnoreCase(category));
    }

    @Override
    public Map<String, List<WarpData>> getCategorisedWarps() {
        return getCategorisedWarps(x -> true);
    }

    @Override
    public Map<String, List<WarpData>> getCategorisedWarps(Predicate<WarpData> warpDataPredicate) {
        Preconditions.checkNotNull(warpDataPredicate);
        Map<String, List<WarpData>> map = store.getWarps().values().stream()
            .filter(warpDataPredicate)
            .collect(Collectors.groupingBy(x -> x.getCategory().orElse("")));
        if (map.containsKey("")) {
            map.put(null, map.get(""));
            map.remove("");
        }

        return map;
    }


    @Override
    public boolean removeWarpCost(String warpName) {
        return store.setWarpCost(warpName, -1);
    }

    @Override
    public boolean setWarpCost(String warpName, int cost) {
        return store.setWarpCost(warpName, cost);
    }

    @Override
    public boolean setWarpCategory(String warpName, @Nullable String category) {
        return store.setWarpCategory(warpName, category);
    }

    @Override
    public Set<String> getWarpNames() {
        return store.getWarps().keySet();
    }

    @Override
    public boolean warpExists(String name) {
        return getWarpNames().stream().filter(x -> x.equalsIgnoreCase(name)).findFirst().isPresent();
    }

    private List<WarpData> getWarpsForCategory(Predicate<WarpData> filter) {
        return store.getWarps().values().stream().filter(filter).collect(Collectors.toList());
    }
}
