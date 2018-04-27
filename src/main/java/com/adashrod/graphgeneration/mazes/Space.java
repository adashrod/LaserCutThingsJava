package com.adashrod.graphgeneration.mazes;

/**
 * A Space represents an element in a 2D array representation of a maze. It knows whether it has walls on any four sides.
 * @author adashrod@gmail.com
 */
public class Space {
    boolean northOpen;
    boolean eastOpen;
    boolean southOpen;
    boolean westOpen;

    /**
     * Removes a wall in the space
     * @param direction which wall to remove
     */
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

    /**
     * @param direction which direction to check for a wall
     * @return true if the wall in the specified direction is open (no wall)
     */
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
