/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class JailConfig {

    @Setting(value = "allowed-commands", comment = "loc:config.jail.commands")
    private List<String> allowedCommands = Lists.newArrayList("m", "msg", "r", "mail", "rules", "info");

    @Setting(value = "mute-when-jailed",comment = "loc:config.jail.muteWhenJailed")
    private boolean muteOnJail = false;

    public List<String> getAllowedCommands() {
        return allowedCommands;
    }

    public boolean isMuteOnJail() {
        return muteOnJail;
    }
}
