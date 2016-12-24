/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.annotations;

import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the class is a command and that the command loader should register it at startup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RegisterCommand {

    /**
     * The subcommand that this represents. Defaults to the {@link AbstractCommand} class.
     *
     * @return The subcommand.
     */
    Class<? extends AbstractCommand> subcommandOf() default AbstractCommand.class;

    /**
     * The aliases for this command.
     *
     * @return Aliases for this command.
     */
    String[] value();

    /**
     * The aliases for this command that should try to be registered as primary aliases, mainly to override
     * Minecraft commands or provide a root level alias for a subcommand.
     *
     * @return Any aliases that should be forced.
     */
    String[] rootAliasRegister() default {};

    /**
     * Sets whether the command should register it's executor. This can be false if there are only child commands.
     *
     * @return <code>true</code> if the executor should be registered.
     */
    boolean hasExecutor() default true;
}
