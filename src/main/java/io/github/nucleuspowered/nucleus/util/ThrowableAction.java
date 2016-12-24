/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

/**
 * A function that accepts nothing, returns nothing, but might throw an {@link Exception}.
 *
 * @param <X> The base type of exception to throw.
 */
@FunctionalInterface
public interface ThrowableAction<X extends Exception> {

    /**
     * Executes an action.
     *
     * @throws X The {@link Exception} that might be thrown
     */
    void action() throws X;
}
