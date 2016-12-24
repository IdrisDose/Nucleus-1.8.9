/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;

import java.lang.annotation.*;

/**
 * Specifies multiple permissions that this command could use. Be sure that no permissions have been set in the
 * {@link org.spongepowered.api.command.spec.CommandSpec.Builder}.
 *
 * <p>
 *     If the mainOverride, prefix and suffix items are set, the default permission that would be generated is
 *     {@code nucleus.prefix.mainOverride.suffix.[]}. Usually, it will just be {@code nucleus.main.[]}, where
 *     main is the primary alias of the command.
 * </p>
 *
 * <p>By default, using this annotation will also allow the command to be run by those with the admin permission.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Permissions {

    /**
     * Arbitrary permissions that could be checked.
     *
     * @return The permissions
     */
    String[] value() default {};

    /**
     * Replaces the command portion of the permission with the specified mainOverride - "prefix.mainOverride.suffix.base"
     *
     * @return The name of the mainOverride to use
     */
    String mainOverride() default "";

    /**
     * The prefix permission to use
     *
     * @return The prefix, or empty string if no prefix
     */
    String prefix() default "";

    /**
     * The suffix permission to use
     *
     * @return The suffix, or empty string if no suffix
     */
    String suffix() default "";

    /**
     * If {@code true}, specifies that selector permissions should be generated. Purely for documentation.
     *
     * @return {@code true} if so.
     */
    boolean supportsSelectors() default false;

    /**
     * The suggested permission level.
     *
     * @return The {@link SuggestedLevel}
     */
    SuggestedLevel suggestedLevel() default SuggestedLevel.ADMIN;
}
