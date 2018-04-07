package com.adashrod.graphgeneration.mazes;

/**
 * Created by arodriguez on 2018-04-04.
 */
public class Space {
    boolean northOpen;
    boolean eastOpen;
    boolean southOpen;
    boolean westOpen;
    boolean exploringNext;
    boolean onPath;

    boolean isUnexplored() {
        return !northOpen && !eastOpen && !southOpen && !westOpen && !exploringNext && !onPath;
    }

    public void openWall(final Direction direction) {
        if (direction == Direction.NORTH) {
            northOpen = true;
        } else if (direction == Direction.EAST) {
            eastOpen = true;
        } else if (direction == Direction.SOUTH) {
            southOpen = true;
        } else if (direction == Direction.WEST) {
            westOpen = true;
        }
    }

    public boolean isOpen(final Direction direction) {
        switch (direction) {
            case NORTH:
                return northOpen;
            case EAST:
                return eastOpen;
            case SOUTH:
                return southOpen;
            case WEST:
                return westOpen;
            default:
                throw new IllegalArgumentException("invalid direction: " + String.valueOf(direction));
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Space[");
        if (northOpen) {
            builder.append("^");
        }
        if (eastOpen) {
            builder.append(">");
        }
        if (southOpen) {
            builder.append("v");
        }
        if (westOpen) {
            builder.append("<");
        }
        return builder.append("]").toString();
    }
}
