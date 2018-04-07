package com.adashrod.graphgeneration.common;

/**
 * A container for two numeric coordinates
 * @author adashrod@gmail.com
 */
public class OrderedPair<T extends Number> {
    public T x, y;

    public OrderedPair(final T x, final T y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("OrderedPair[x=%s, y=%s]", x, y);
    }
}
