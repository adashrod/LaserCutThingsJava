package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.util.EnumMap;
import java.util.Map;

/**
 * four directions for relating {@link Space}s to each other
 */
public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    private static final Map<Direction, Direction> OPPOSITES = new EnumMap<>(Direction.class);

    static {
        OPPOSITES.put(NORTH, SOUTH);
        OPPOSITES.put(SOUTH, NORTH);
        OPPOSITES.put(EAST, WEST);
        OPPOSITES.put(WEST, EAST);
    }

    public Direction opposite() {
        return OPPOSITES.get(this);
    }

    /**
     * Determines the direction that relates "from" to "to", e.g. if "to" is to the EAST of "from", EAST is returned
     * @param from
     * @param to
     * @return
     */
    public static Direction determineDirection(final OrderedPair<?> from, final OrderedPair<?> to) {
        final int xComp = Double.compare(from.x.doubleValue(), to.x.doubleValue());
        if (xComp < 0) { return Direction.EAST; }
        if (xComp > 0) { return Direction.WEST; }
        final int yComp = Double.compare(from.y.doubleValue(), to.y.doubleValue());
        if (yComp < 0) { return Direction.SOUTH; }
        if (yComp > 0) { return Direction.NORTH; }
        throw new IllegalArgumentException(String.format("Indeterminate: the 2 OrderedPairs couldn't be compared: %s, %s",
            from, to));
    }
}
