package com.adashrod.graphgeneration.mazes;

import java.util.Collection;
import java.util.HashSet;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.NORTH;
import static com.adashrod.graphgeneration.mazes.Direction.SOUTH;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;

/**
 * Similar to a {@link Maze}, this model is a grid-based representation of a maze. The difference is that this has grid
 * spaces for both path space and walls. Each space that represents part of a wall has information about whether it is
 * one or more ends of a wall or part of the middle.
 * @author adashrod@gmail.com
 */
public class TopDownRectangularWallModel {
    final Space[][] grid;
    double wallWidth, hallWidth;

    public TopDownRectangularWallModel(final int width, final int height) {
        grid = new Space[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Space();
            }
        }
    }

    public static class Space {
        boolean isWall;
        final Collection<Direction> endDirections = new HashSet<>(); // todo: get rid of direct access

        @Override
        public String toString() {
            return String.format("Space[%s,%s]", isWall ? "#" : " ", endDirections.isEmpty() ? "" : endDirections);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(grid.length * grid[0].length);
        for (final Space[] row: grid) {
            for (final Space space: row) {
                if (space.isWall && space.endDirections.isEmpty()) {
                    builder.append("#");
                } else if (!space.isWall) {
                    builder.append(" ");
                } else {
                    if (space.endDirections.contains(NORTH)) {
                        builder.append(space.endDirections.contains(SOUTH) ? "V" : "^");
                    } else if (space.endDirections.contains(SOUTH)) {
                        builder.append("v");
                    } else if (space.endDirections.contains(WEST)) {
                        builder.append(space.endDirections.contains(EAST) ? "H" : "[");
                    } else if (space.endDirections.contains(EAST)) {
                        builder.append("]");
                    } else {
                        builder.append("?");
                    }
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
