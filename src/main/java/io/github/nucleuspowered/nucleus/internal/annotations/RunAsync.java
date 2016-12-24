/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import java.lang.annotation.*;

/**
 * Any {@link io.github.nucleuspowered.nucleus.internal.command.AbstractCommand} that is decorated with this annotation will be
 * run on an async thread. This should only be used for thread-safe operations.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RunAsync {
}
