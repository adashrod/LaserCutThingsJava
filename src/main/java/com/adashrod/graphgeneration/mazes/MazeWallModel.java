package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This maze class is a representation of a maze based on a list of walls. Each wall has a start point and end point.
 * The walls should not intersect, except possibly at the ends with T- and L-shaped intersections. A 4-way intersection
 * should always consist of three walls.
 * @author adashrod@gmail.com
 */
public class MazeWallModel {
    final Collection<Wall> walls = new ArrayList<>();

    public void addWall(final Wall wall) {
        walls.add(wall);
    }

    /**
     * A wall within a MazeWallModel. The model is a 2D top-down representation of a maze. Walls have no depth
     */
    public static class Wall {
        OrderedPair<Double> start;
        OrderedPair<Double> end;

        public Wall(final OrderedPair<Double> start, final OrderedPair<Double> end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("Wall[%s to %s]", start, end);
        }
    }
}
