/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import org.spongepowered.api.entity.living.player.Player;

import java.lang.annotation.*;

/**
 * This annotation is applied to commands where an argument should be checked for a player that might be AFK, and the
 * command sender could benefit from knowing this. The values are strings that refer to the keys of command arguments that
 * might return {@link Player}s.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NotifyIfAFK {

    String[] value();
}
