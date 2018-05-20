package com.adashrod.lasercutthings.common;

import java.util.Objects;

/**
 * A container for two numeric coordinates
 * @author adashrod@gmail.com
 */
public class OrderedPair<T extends Number> {
    public T x, y;

    public OrderedPair(final T x, final T y) {
        this.x = Objects.requireNonNull(x, "x can't be null");
        this.y = Objects.requireNonNull(y, "y can't be null");
    }

    @Override
    public String toString() {
        return String.format("OrderedPair[x=%s, y=%s]", x, y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(x);
        result = prime * result + Objects.hashCode(y);

        return result;
    }

    @Override
    public boolean equals(final Object anObject) {
        if (!(anObject instanceof OrderedPair)) {
            return false;
        }
        final OrderedPair anOrderedPair = (OrderedPair) anObject;
        return x.equals(anOrderedPair.x) && y.equals(anOrderedPair.y);
    }

}
