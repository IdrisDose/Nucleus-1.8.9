/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.config;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.configurate.annotations.ProcessSetting;
import io.github.nucleuspowered.nucleus.configurate.settingprocessor.LowercaseListSettingProcessor;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class BlockSpawnsConfig {

    @Setting(value = "block-vanilla-mobs", comment = "loc:config.blockspawn.vanilla")
    private boolean blockVanillaMobs = false;

    @Setting(value = "block-mobs-with-ids", comment = "loc:config.blockspawn.ids")
    @ProcessSetting(LowercaseListSettingProcessor.class)
    private List<String> idsToBlock = Lists.newArrayList();

    public boolean isBlockVanillaMobs() {
        return blockVanillaMobs;
    }

    public List<String> getIdsToBlock() {
        return idsToBlock;
    }
}
