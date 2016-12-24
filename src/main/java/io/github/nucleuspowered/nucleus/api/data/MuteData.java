/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data;

import io.github.nucleuspowered.nucleus.api.data.interfaces.EndTimestamp;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public final class MuteData extends EndTimestamp {

    @Setting
    private UUID muter;

    @Setting
    private Long endtimestamp;

    @Setting
    private String reason;

    @Setting
    private Long timeFromNextLogin;

    // For Configurate
    public MuteData() { }

    public MuteData(UUID muter, String reason) {
        this.muter = muter;
        this.reason = reason;
    }

    /**
     * Creates the data.
     *
     * @param muter The UUID of the muter
     * @param endtimestamp The end timestamp
     * @param reason The reason
     */
    public MuteData(UUID muter, String reason, Instant endtimestamp) {
        this(muter, reason);
        this.endtimestamp = endtimestamp.getEpochSecond();
    }

    /**
     * Creates the data.
     *
     * @param muter The UUID of the muter
     * @param reason The reason
     * @param timeFromNextLogin The time to mute for from next login.
     */
    public MuteData(UUID muter, String reason, Duration timeFromNextLogin) {
        this(muter, reason);
        this.timeFromNextLogin = timeFromNextLogin.getSeconds();
    }

    public String getReason() {
        return reason;
    }

    /**
     * Gets the timestamp for the end of the mute.
     *
     * @return An {@link Instant}
     */
    public Optional<Instant> getEndTimestamp() {
        if (endtimestamp == null) {
            return Optional.empty();
        }

        return Optional.of(Instant.ofEpochSecond(endtimestamp));
    }

    public UUID getMuter() {
        return muter;
    }

    public Optional<Duration> getTimeFromNextLogin() {
        if (timeFromNextLogin == null) {
            return Optional.empty();
        }

        return Optional.of(Duration.of(timeFromNextLogin, ChronoUnit.SECONDS));
    }
}
