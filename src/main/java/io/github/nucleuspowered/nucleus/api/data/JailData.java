/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data;

import io.github.nucleuspowered.nucleus.api.data.interfaces.EndTimestamp;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public final class JailData extends EndTimestamp {

    @Setting
    private UUID jailer;

    @Setting
    private String jailName;

    @Setting
    private String reason;

    @Setting
    private double previousx = 0;

    @Setting
    private double previousy = -1;

    @Setting
    private double previousz = 0;

    @Setting
    private UUID world;

    // Configurate
    public JailData() { }

    public JailData(UUID jailer, String jailName, String reason, Location<World> previousLocation) {
        this.jailer = jailer;
        this.reason = reason;
        this.jailName = jailName;

        if (previousLocation != null) {
            this.world = previousLocation.getExtent().getUniqueId();
            this.previousx = previousLocation.getX();
            this.previousy = previousLocation.getY();
            this.previousz = previousLocation.getZ();
        }
    }

    public JailData(UUID jailer, String jailName, String reason, Location<World> previousLocation, Instant endTimestamp) {
        this(jailer, jailName, reason, previousLocation);
        this.endtimestamp = endTimestamp.getEpochSecond();
    }

    public JailData(UUID jailer, String jailName, String reason, Location<World> previousLocation, Duration timeFromNextLogin) {
        this(jailer, jailName, reason, previousLocation);
        this.timeFromNextLogin = timeFromNextLogin.getSeconds();
    }

    public void setPreviousLocation(Location<World> previousLocation) {
        this.world = previousLocation.getExtent().getUniqueId();
        this.previousx = previousLocation.getX();
        this.previousy = previousLocation.getY();
        this.previousz = previousLocation.getZ();
    }

    public String getReason() {
        return reason;
    }

    public String getJailName() {
        return jailName;
    }

    public UUID getJailer() {
        return jailer;
    }

    public Optional<Location<World>> getPreviousLocation() {
        if (world != null) {
            Optional<World> ow = Sponge.getServer().getWorld(world);
            if (ow.isPresent() && previousx != 0 && previousy != -1 && previousz != 0) {
                return Optional.of(new Location<>(ow.get(), previousx, previousy, previousz));
            }
        }

        return Optional.empty();
    }

    public Optional<Duration> getTimeLeft() {
        if (endtimestamp == null && timeFromNextLogin == null) {
            return Optional.empty();
        }

        if (endtimestamp != null) {
            return Optional.of(Duration.between(Instant.ofEpochSecond(endtimestamp), Instant.now()));
        }

        return Optional.of(Duration.of(timeFromNextLogin, ChronoUnit.SECONDS));
    }
}
