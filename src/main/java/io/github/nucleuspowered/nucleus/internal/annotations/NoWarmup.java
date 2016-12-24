/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import java.lang.annotation.*;

/**
 * Marks a command as not having a warmup, even if one is defined.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface NoWarmup {

    /**
     * Whether to generate the Warmup Config entry.
     *
     * @return <code>true</code> if so.
     */
    boolean generateConfigEntry() default false;
}
