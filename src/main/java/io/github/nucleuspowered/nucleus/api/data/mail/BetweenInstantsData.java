/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data.mail;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ConfigSerializable
public final class BetweenInstantsData {

    private Long from;
    private Long to;

    public BetweenInstantsData(@Nullable Instant from, @Nullable Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            Instant temp = from;
            from = to;
            to = temp;
        }

        this.from = from == null ? null : from.getEpochSecond();
        this.to = to == null ? null : to.getEpochSecond();
    }

    public Optional<Instant> from() {
        return Optional.ofNullable(Instant.ofEpochSecond(from));
    }

    public Optional<Instant> to() {
        return Optional.ofNullable(Instant.ofEpochSecond(to));
    }

    public Optional<Duration> duration() {
        if (from == null || to == null) {
            return Optional.empty();
        }

        return Optional.of(Duration.of(to - from, ChronoUnit.SECONDS));
    }
}
