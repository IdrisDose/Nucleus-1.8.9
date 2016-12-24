/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class KitConfig {

    @Setting(value = "separate-permissions", comment = "loc:config.kits.separate")
    private boolean separatePermissions = false;

    public boolean isSeparatePermissions() {
        return separatePermissions;
    }
}
