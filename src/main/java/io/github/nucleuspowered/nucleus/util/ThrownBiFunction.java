/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

/**
 * A function that accepts two arguments and returns a result, but can also throw a checked exception.
 *
 * @param <A> The first argument type.
 * @param <B> The second argument type.
 * @param <R> The type of the result.
 * @param <T> The type of {@link Exception} that could be thrown.
 */
@FunctionalInterface
public interface ThrownBiFunction<A, B, R, T extends Exception> {

    R accept(A a, B b) throws T;
}
