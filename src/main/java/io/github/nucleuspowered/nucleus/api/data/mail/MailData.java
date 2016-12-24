/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data.mail;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Instant;
import java.util.UUID;

@ConfigSerializable
public class MailData {

    @Setting
    private UUID uuid;

    @Setting
    private long date;

    @Setting
    private String message;

    public MailData() { }

    public MailData(UUID uuid, Instant date, String message) {
        this.uuid = uuid;
        this.date = date.toEpochMilli();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Instant getDate() {
        return Instant.ofEpochMilli(date);
    }

    public UUID getUuid() {
        return uuid;
    }
}
