/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import io.github.nucleuspowered.nucleus.Nucleus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * A {@link ConditionalListener} allows Nucleus to not load unnecessary listeners, when listeners only exist for compatibility fixes etc. This allows
 * us to try to keep the number of required listeners down to a minimum.
 *
 * <p>
 *     This may get loaded and/or unloaded during a reload event.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ConditionalListener {

    Class<? extends Predicate<Nucleus>> value();
}
