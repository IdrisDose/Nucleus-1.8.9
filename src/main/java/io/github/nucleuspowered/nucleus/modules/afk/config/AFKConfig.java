/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class AFKConfig {

    @Setting(value = "afktime", comment = "loc:config.afk.time")
    private long afkTime = 300;

    @Setting(value = "afktimetokick", comment = "loc:config.afk.timetokick")
    private long afkTimeToKick = 0;

    @Setting(value = "afk-when-vanished", comment = "loc:config.afk.whenvanished")
    private boolean afkOnVanish = false;

    @Setting(value = "alert-on-command-send", comment = "loc:config.afk.oncommandsend")
    private boolean alertSenderOnAfk = true;

    @Setting(value = "broadcast-to-all-on-kick", comment = "loc:config.afk.broadcastonkick")
    private boolean broadcastOnKick = true;

    @Setting(value = "messages", comment = "loc:config.afk.messages.base")
    private MessagesConfig messages = new MessagesConfig();

    public long getAfkTime() {
        return afkTime;
    }

    public long getAfkTimeToKick() {
        return afkTimeToKick;
    }

    public boolean isAfkOnVanish() {
        return afkOnVanish;
    }

    public boolean isAlertSenderOnAfk() {
        return alertSenderOnAfk;
    }

    public boolean isBroadcastOnKick() {
        return broadcastOnKick;
    }

    public MessagesConfig getMessages() {
        return messages;
    }
}
