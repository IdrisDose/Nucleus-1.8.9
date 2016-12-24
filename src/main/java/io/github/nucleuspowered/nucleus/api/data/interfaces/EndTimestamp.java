/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data.interfaces;

import ninja.leaping.configurate.objectmapping.Setting;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public abstract class EndTimestamp {

    @Setting
    protected Long endtimestamp;

    @Setting
    protected Long timeFromNextLogin;

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

    public Optional<Duration> getTimeFromNextLogin() {
        if (timeFromNextLogin == null) {
            return Optional.empty();
        }

        return Optional.of(Duration.of(timeFromNextLogin, ChronoUnit.SECONDS));
    }

    public void nextLoginToTimestamp() {
        if (timeFromNextLogin != null && endtimestamp == null) {
            endtimestamp = Instant.now().plus(timeFromNextLogin, ChronoUnit.SECONDS).getEpochSecond();
            timeFromNextLogin = null;
        }
    }
}
