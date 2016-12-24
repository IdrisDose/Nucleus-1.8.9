/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class LoggerTargetConfig {

    @Setting("player")
    private boolean logPlayer = true;

    @Setting("command-block")
    private boolean logCommandBlock = true;

    @Setting("console")
    private boolean logConsole = true;

    @Setting("other-source")
    private boolean logOther = true;

    public boolean isLogPlayer() {
        return logPlayer;
    }

    public boolean isLogCommandBlock() {
        return logCommandBlock;
    }

    public boolean isLogConsole() {
        return logConsole;
    }

    public boolean isLogOther() {
        return logOther;
    }
}
