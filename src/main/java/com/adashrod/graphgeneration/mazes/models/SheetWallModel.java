package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This model class is a model of the walls and floor that compose a maze with the intention that each piece (wall or
 * floor) is of a constant thickness (the thickness of the physical sheet that will be used) and the walls will be
 * rotated upward and attached to the floor.
 * @author adashrod@gmail.com
 */
public class SheetWallModel {
    public final Shape floorNotches = new Shape();
    public final Shape floorOutline = new Shape();
    private final Collection<Shape> walls = new ArrayList<>();

    public SheetWallModel addShape(final Shape shape) {
        walls.add(shape);
        return this;
    }

    /**
     * A shape is a collection of closed paths. It can have one or many paths that are each not connected to each other.
     */
    public static class Shape {
        public final List<Path> paths = new ArrayList<>();

        public Shape() {}

        public Shape(final Path path) {
            paths.add(path);
        }

        public Shape addPath(final Path path) {
            paths.add(path);
            return this;
        }

        public Shape translate(final OrderedPair<BigDecimal> delta) {
            paths.forEach((final Path path) -> {
                path.points.forEach((final OrderedPair<BigDecimal> point) -> {
                    point.x = point.x.add(delta.x);
                    point.y = point.y.add(delta.y);
                });
            });
            return this;
        }
    }

    /**
     * A path is a set of points. The implication is that there is a line from point 0 to point 1, point 1 to
     * point 2, ..., point n - 1 to point n, and (if isClosed == true) point n to point 0
     */
    public static class Path {
        public final List<OrderedPair<BigDecimal>> points = new ArrayList<>();
        public boolean isClosed = true;

        public Path() {}

        public Path(final OrderedPair<BigDecimal> from, final OrderedPair<BigDecimal> to) {
            points.add(from);
            points.add(to);
        }

        public Path addPoint(final OrderedPair<BigDecimal> point) {
            points.add(point);
            return this;
        }

        public Path setClosed(final boolean isClosed) {
            this.isClosed = isClosed;
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder pointsBuilder = new StringBuilder();
            for (final OrderedPair<BigDecimal> point: points) {
                pointsBuilder.append("(").append(point.x).append(", ").append(point.y).append(") -> ");
            }
            pointsBuilder.delete(pointsBuilder.length() - 4, pointsBuilder.length());
            return String.format("ClosedPath[%s]", pointsBuilder.toString());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hashCode(points);

            return result;
        }

        @Override
        public boolean equals(final Object anObject) {
            if (!(anObject instanceof Path)) {
                return false;
            }
            final Path aPath = (Path) anObject;
            return points.equals(aPath.points);
        }
    }
}
