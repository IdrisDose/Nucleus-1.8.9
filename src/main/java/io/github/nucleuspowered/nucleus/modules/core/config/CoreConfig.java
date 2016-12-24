/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.config;

import io.github.nucleuspowered.nucleus.configurate.annotations.DoNotGenerate;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CoreConfig {

    @Setting(value = "debug-mode", comment = "loc:config.debugmode")
    private boolean debugmode = false;

    @Setting(value = "use-custom-message-file", comment = "loc:config.custommessages")
    private boolean custommessages = false;

    @Setting(value = "warmup-canceling", comment = "loc:config.core.warmup.info")
    private WarmupConfig warmupConfig = new WarmupConfig();

    @Setting(value = "command-on-name-click", comment = "loc:config.core.commandonname")
    private String commandOnNameClick = "/msg {{player}}";

    @Setting(value = "kick-on-stop")
    private KickOnStopConfig kickOnStop = new KickOnStopConfig();

    @DoNotGenerate
    @Setting(value = "enable-doc-gen")
    private boolean enableDocGen = false;

    @DoNotGenerate
    @Setting(value = "simulate-error-on-startup")
    private boolean errorOnStartup = false;

    @Setting(value = "safe-teleport-check", comment = "loc:config.core.safeteleport")
    private SafeTeleportConfig safeTeleportConfig = new SafeTeleportConfig();

    public boolean isDebugmode() {
        return debugmode;
    }

    public boolean isCustommessages() {
        return custommessages;
    }

    public WarmupConfig getWarmupConfig() {
        return warmupConfig;
    }

    public String getCommandOnNameClick() {
        return commandOnNameClick;
    }

    public boolean isKickOnStop() {
        return kickOnStop.isKickOnStop();
    }

    public String getKickOnStopMessage() {
        return kickOnStop.getKickOnStopMessage();
    }

    public boolean isEnableDocGen() {
        return enableDocGen;
    }

    public boolean isErrorOnStartup() {
        return errorOnStartup;
    }

    public SafeTeleportConfig getSafeTeleportConfig() {
        return safeTeleportConfig;
    }
}
