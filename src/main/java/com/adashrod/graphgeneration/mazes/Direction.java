package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by arodriguez on 2018-04-04.
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

    public static Direction determineDirection(final OrderedPair<?> from, final OrderedPair<?> to) {
        final int xComp = Double.compare(from.x.doubleValue(), to.x.doubleValue());
        if (xComp < 0) { return Direction.EAST; }
        if (xComp > 0) { return Direction.WEST; }
        final int yComp = Double.compare(from.y.doubleValue(), to.y.doubleValue());
        if (yComp < 0) { return Direction.SOUTH; }
        if (yComp > 0) { return Direction.NORTH; }
        throw new IllegalArgumentException("dude wtf");
    }
}
