/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.configurate.datatypes.ItemDataNode;
import io.github.nucleuspowered.nucleus.configurate.datatypes.item.BlacklistNode;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;
import io.github.nucleuspowered.nucleus.util.Tuples;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemDataService extends Service<Map<String, ItemDataNode>> {

    private Map<String, String> aliasToItemIdCache = null;
    private Map<String, BlacklistNode> blacklistCache = null;
    private Map<CatalogType, BlacklistNode> blacklistTypeCache = null;

    public ItemDataService(DataProvider<Map<String, ItemDataNode>> dataProvider) throws Exception {
        super(dataProvider, true);
    }

    @Override
    public boolean load() {
        clearCache();
        return super.load();
    }

    @Override
    public boolean save() {
        clearCache();
        return super.save();
    }

    public Set<String> getAliases() {
        return getCache().keySet();
    }

    public ItemDataNode getDataForItem(ItemStackSnapshot itemStackSnapshot) {
        return getDataForItem(getIdFromSnapshot(itemStackSnapshot));
    }

    public ItemDataNode getDataForItem(String id) {
        Preconditions.checkNotNull(id);
        return data.getOrDefault(id.toLowerCase(), new ItemDataNode());
    }

    public void setDataForItem(ItemStackSnapshot itemStackSnapshot, ItemDataNode node) {
        setDataForItem(getIdFromSnapshot(itemStackSnapshot), node);
    }

    public void setDataForItem(String id, ItemDataNode node) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(node);
        data.put(id, node);
        save();
    }

    public void resetDataForItem(ItemStackSnapshot itemStackSnapshot) {
        resetDataForItem(getIdFromSnapshot(itemStackSnapshot));
    }

    public void resetDataForItem(String id) {
        Preconditions.checkNotNull(id);
        data.remove(id);
        save();
    }

    public Optional<String> getIdFromAlias(String alias) {
        return Optional.ofNullable(getCache().get(alias));
    }

    public Optional<BlacklistNode> getBlacklistFor(String id) {
        return Optional.ofNullable(getBlacklistCache().get(id));
    }

    public Map<CatalogType, BlacklistNode> getAllBlacklistedItemsByCatalogType() {
        return ImmutableMap.copyOf(getBlacklistItemCache());
    }

    public Map<String, BlacklistNode> getAllBlacklistedItems() {
        return ImmutableMap.copyOf(getBlacklistCache());
    }

    private Map<CatalogType, BlacklistNode> getBlacklistItemCache() {
        if (this.blacklistTypeCache == null) {
            this.blacklistTypeCache = getBlacklistCache().entrySet().stream()
                .map(x -> {
                    Optional<CatalogType> catalogType = Util.getCatalogTypeForItemFromId(x.getKey());
                    if (catalogType.isPresent()) {
                        return Tuples.of(catalogType.get(), x.getValue());
                    } else {
                        return Tuples.of(ItemTypes.NONE, x.getValue());
                    }
                }).filter(x -> !x.getFirst().equals(ItemTypes.NONE)).collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
        }

        return this.blacklistTypeCache;
    }

    private Map<String, BlacklistNode> getBlacklistCache() {
        if (this.blacklistCache == null) {
            this.blacklistCache = data.entrySet().stream().filter(x -> x.getValue().isBlacklisted())
                .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().getBlacklist()));
        }

        return this.blacklistCache;
    }

    private Map<String, String> getCache() {
        if (aliasToItemIdCache == null) {
            aliasToItemIdCache = data.entrySet().stream()
                    .flatMap(k -> k.getValue().getAliases().stream().map(i -> Tuple.of(i, k.getKey())))
                    .collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
        }

        return aliasToItemIdCache;
    }

    private String getIdFromSnapshot(ItemStackSnapshot stackSnapshot) {
        Optional<BlockState> blockState = stackSnapshot.get(Keys.ITEM_BLOCKSTATE);
        if (blockState.isPresent()) {
            return blockState.get().getId().toLowerCase();
        }

        return stackSnapshot.getType().getId();
    }

    private void clearCache() {
        aliasToItemIdCache = null;
        blacklistCache = null;
        blacklistTypeCache = null;
    }
}
