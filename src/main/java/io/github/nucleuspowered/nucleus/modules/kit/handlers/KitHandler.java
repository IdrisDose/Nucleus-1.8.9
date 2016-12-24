/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.handlers;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.Kit;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import io.github.nucleuspowered.nucleus.configurate.datatypes.KitDataNode;
import io.github.nucleuspowered.nucleus.dataservices.KitService;

import java.util.Optional;
import java.util.Set;

public class KitHandler implements NucleusKitService {

    @Inject private KitService store;

    @Override
    public Set<String> getKitNames() {
        return store.getKits().keySet();
    }

    @Override
    public Optional<Kit> getKit(String name) {
        return Optional.ofNullable(store.getKit(name).orElse(null));
    }

    @Override
    public boolean removeKit(String kitName) {
        if (store.removeKit(kitName)) {
            store.save();
            return true;
        }

        return false;
    }

    @Override
    public synchronized void saveKit(String kitName, Kit kit) {
        Preconditions.checkArgument(kit instanceof KitDataNode);
        Util.getKeyIgnoreCase(store.getKits(), kitName).ifPresent(store::removeKit);
        store.addKit(kitName, (KitDataNode)kit);
        store.save();
    }

    @Override
    public Kit createKit() {
        return new KitDataNode();
    }
}
