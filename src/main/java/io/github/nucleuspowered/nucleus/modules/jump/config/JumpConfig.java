/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class JumpConfig {

    @Setting(value = "max-jump-distance", comment = "loc:config.jump.maxdist")
    private int maxjump = 350;

    @Setting(value = "max-thru-distance", comment = "loc:config.thru.maxdist")
    private int maxthru = 25;

    public int getMaxJump() {
        return maxjump;
    }

    public int getMaxThru() {
        return maxthru;
    }
}
