package com.adashrod.lasercutthings.mazes.models;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.Direction;

import java.util.ArrayList;
import java.util.Collection;

import static com.adashrod.lasercutthings.mazes.Direction.NORTH;
import static com.adashrod.lasercutthings.mazes.Direction.WEST;

/**
 * This model class is a representation of a maze based on a list of walls. Each wall has a start point and end point.
 * The walls should not intersect and cross; they should only intersect end-to-end with T- and L-shaped intersections.
 * A 4-way intersection should always consist of three walls. T-shaped intersections should be split into 3 parts if the
 * part of the intersection analogous to the top of the letter T is not in the favored direction
 * @author adashrod@gmail.com
 */
public class LinearWallModel {
    public final int width, height;
    public final Collection<Wall> walls = new ArrayList<>();
    public final boolean favorEwWalls;

    public LinearWallModel(final int width, final int height, final boolean favorEwWalls) {
        this.width = width;
        this.height = height;
        this.favorEwWalls = favorEwWalls;
    }

    public void addWall(final Wall wall) {
        walls.add(wall);
    }

    /**
     * A wall within a LinearWallModel. The model is a 2D top-down representation of a maze. Walls have no depth
     */
    public static class Wall {
        public OrderedPair<Integer> start;
        public OrderedPair<Integer> end;

        public Wall(final OrderedPair<Integer> start, final OrderedPair<Integer> end) {
            final Direction direction = Direction.determineDirection(start, end);
            // ensure that walls are always represented as left-to-right or top-to-bottom
            if (direction == NORTH || direction == WEST) {
                this.start = end;
                this.end = start;
            } else {
                this.start = start;
                this.end = end;
            }
        }

        @Override
        public String toString() {
            return String.format("Wall[%s to %s]", start, end);
        }
    }
}
