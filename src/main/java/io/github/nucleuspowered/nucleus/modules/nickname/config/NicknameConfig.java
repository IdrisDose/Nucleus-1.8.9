/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class NicknameConfig {

    @Setting(value = "min-nickname-length", comment = "loc:config.nicknames.min")
    private int minNicknameLength = 3;

    @Setting(value = "max-nickname-length", comment = "loc:config.nicknames.max")
    private int maxNicknameLength = 20;

    @Setting(value = "prefix", comment = "loc:config.nicknames.prefix")
    private String prefix = "&b~";

    public int getMinNicknameLength() {
        return minNicknameLength;
    }

    public int getMaxNicknameLength() {
        return maxNicknameLength;
    }

    public String getPrefix() {
        return prefix;
    }
}
