package com.adashrod.lasercutthings.mazes.models;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to a {@link Maze}, this model is a grid-based representation of a maze. The difference is that this has grid
 * spaces for both path space and walls. Each space that represents part of a wall has information about whether it is
 * one or more ends of a wall or part of the middle.
 * @author adashrod@gmail.com
 */
public class RectangularWallModel {
    public final List<Wall> walls = new ArrayList<>();
    public final int width, height;

    public RectangularWallModel(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public RectangularWallModel addWall(final Wall wall) {
        walls.add(wall);
        return this;
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
