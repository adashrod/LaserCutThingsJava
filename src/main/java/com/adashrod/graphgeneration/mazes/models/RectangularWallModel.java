package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class RectangularWallModel {
    // todo: grid is only used for printing and by Tdrwmg to create walls; could refactor it out of here into Tdrwmg and printing into MazePrinter
    public final Space[][] grid;
    public final List<Wall> walls = new ArrayList<>();

    public RectangularWallModel(final int width, final int height) {
        grid = new Space[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Space();
            }
        }
    }

    public RectangularWallModel addWall(final Wall wall) {
        walls.add(wall);
        return this;
    }

    public static class Space {
        public boolean isWall;
        public final Collection<Direction> endDirections = new ArrayList<>(); // todo: get rid of direct access

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

    public static class Wall {
        public final OrderedPair<Integer> start, end;
        public final int length;
        private final Direction wallDirection;

        public Wall(final OrderedPair<Integer> start, final OrderedPair<Integer> end, final Direction wallDirection) {
            this.start = start;
            this.end = end;
            length = Math.max(end.y - start.y + 1, end.x - start.x + 1);
            this.wallDirection = wallDirection;
        }

        public Direction getWallDirection() {
            return wallDirection;
        }
    }
}
