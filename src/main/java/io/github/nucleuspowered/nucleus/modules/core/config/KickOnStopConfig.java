/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class KickOnStopConfig {
    @Setting(value = "enabled", comment = "loc:config.core.kickonstop.flag")
    private boolean kickOnStop = false;

    @Setting(value = "message", comment = "loc:config.core.kickonstop.message")
    private String kickOnStopMessage = "Server closed";

    public boolean isKickOnStop() {
        return kickOnStop;
    }

    public String getKickOnStopMessage() {
        return kickOnStopMessage;
    }
}
