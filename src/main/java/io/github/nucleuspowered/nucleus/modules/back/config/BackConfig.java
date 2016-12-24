/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class BackConfig {

    @Setting(value = "on-death", comment = "loc:config.back.ondeath")
    private boolean onDeath = true;

    @Setting(value = "on-teleport", comment = "loc:config.back.onteleport")
    private boolean onTeleport = true;

    @Setting(value = "on-portal", comment = "loc:config.back.onportal")
    private boolean onPortal = false;

    public boolean isOnDeath() {
        return onDeath;
    }

    public boolean isOnTeleport() {
        return onTeleport;
    }

    public boolean isOnPortal() {
        return onPortal;
    }
}
