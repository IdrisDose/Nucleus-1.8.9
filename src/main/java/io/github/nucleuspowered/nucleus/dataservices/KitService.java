/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.Kit;
import io.github.nucleuspowered.nucleus.configurate.datatypes.KitConfigDataNode;
import io.github.nucleuspowered.nucleus.configurate.datatypes.KitDataNode;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class KitService extends Service<KitConfigDataNode> {

    public KitService(DataProvider<KitConfigDataNode> dataProvider) throws Exception {
        super(dataProvider, false);
    }

    public Optional<KitDataNode> getKit(String name) {
        Optional<String> key = Util.getKeyIgnoreCase(data.getKits(), name);
        if (key.isPresent()) {
            return Optional.of(data.getKits().get(key.get()));
        }

        return Optional.empty();
    }

    public Map<String, Kit> getKits() {
        return ImmutableMap.copyOf(data.getKits());
    }

    public boolean addKit(String name, KitDataNode kit) {
        if (data.getKits().keySet().stream().anyMatch(name::equalsIgnoreCase)) {
            return false;
        }

        data.getKits().put(name, kit);
        return true;
    }

    public boolean removeKit(String name) {
        Map<String, KitDataNode> msk = data.getKits();
        Optional<String> key = msk.keySet().stream().filter(name::equalsIgnoreCase).findFirst();
        return key.isPresent() && data.getKits().remove(key.get()) != null;
    }

    public List<ItemStackSnapshot> getFirstKit() {
        return data.getFirstKit();
    }

    public void setFirstKit(@Nullable List<ItemStackSnapshot> stack) {
        if (stack == null) {
            stack = Lists.newArrayList();
        }

        data.setFirstKit(stack);
    }
}
