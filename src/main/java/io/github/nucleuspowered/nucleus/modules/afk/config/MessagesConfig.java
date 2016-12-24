/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MessagesConfig {

    @Setting("on-afk")
    private String afkMessage = "&7*&f{{displayname}} &7has gone AFK.";

    @Setting("on-return")
    private String returnAfkMessage = "&7*&f{{displayname}} &7is no longer AFK.";

    @Setting("on-command")
    private String onCommand = "&f{{displayname}} &7is currently AFK and may not respond quickly.";

    @Setting(value = "on-kick", comment = "loc:config.afk.messagetobroadcastonkick")
    private String onKick = "&f{{displayname}} &7has been kicked for being AFK too long.";

    @Setting(value = "kick-message-to-player", comment = "loc:config.afk.playerkicked")
    private String kickMessage = "You have been kicked for being AFK for too long.";

    public String getAfkMessage() {
        return afkMessage;
    }

    public String getReturnAfkMessage() {
        return returnAfkMessage;
    }

    public String getOnCommand() {
        return onCommand;
    }

    public String getOnKick() {
        return onKick;
    }

    public String getKickMessage() {
        return kickMessage;
    }
}
