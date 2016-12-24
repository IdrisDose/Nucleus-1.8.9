/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import org.spongepowered.api.util.Tuple;

public final class Tuples {

    private Tuples() {}

    public static <A, B> Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

    public static <A, B, C> Tri<A, B, C> of(A a, B b, C c) {
        return new Tri<>(a, b, c);
    }

    public static <A, B, C, D> Quad<A, B, C, D> of(A a, B b, C c, D d) {
        return new Quad<>(a, b, c, d);
    }

    public static class Tri<A, B, C> {

        private final A first;
        private final B second;
        private final C third;

        private Tri(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        public C getThird() {
            return third;
        }
    }

    public static class Quad<A, B, C, D> extends Tri<A, B, C> {

        private final D fourth;

        private Quad(A first, B second, C third, D fourth) {
            super(first, second, third);
            this.fourth = fourth;
        }

        public D getFourth() {
            return fourth;
        }
    }
}
