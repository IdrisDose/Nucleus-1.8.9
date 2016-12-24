/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

/**
 * A function that accepts three arguments and returns a result.
 *
 * @param <A> The first argument type.
 * @param <B> The second argument type.
 * @param <C> The third argument type.
 * @param <R> The type of the result.
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

    R accept(A a, B b, C c);
}
