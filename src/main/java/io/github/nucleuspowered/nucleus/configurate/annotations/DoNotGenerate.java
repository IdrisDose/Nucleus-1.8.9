/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.annotations;

import java.lang.annotation.*;

/**
 * Tells the Nucleus Object Mapper to not create this field at runtime if the default value is set.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DoNotGenerate {
}
