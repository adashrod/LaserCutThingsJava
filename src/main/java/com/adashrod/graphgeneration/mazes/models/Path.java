package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * A path is a set of points. The implication is that there is a line from point 0 to point 1, point 1 to
 * point 2, ..., point n - 1 to point n, and (if isClosed == true) point n to point 0
 */
public class Path {
    public final List<OrderedPair<BigDecimal>> points = new ArrayList<>();
    public boolean isClosed = true;

    public Path() {}

    public Path(final OrderedPair<BigDecimal> from, final OrderedPair<BigDecimal> to) {
        points.add(from);
        points.add(to);
        isClosed = false;
    }

    public Path addPoint(final OrderedPair<BigDecimal> point) {
        points.add(point);
        return this;
    }

    public Path setClosed(final boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    public BigDecimal findWidth() {
        BigDecimal minimum = null, maximum = null;
        for (final OrderedPair<BigDecimal> point: points) {
            if (minimum == null || minimum.compareTo(point.x) > 0) {
                minimum = point.x;
            }
            if (maximum == null || maximum.compareTo(point.x) < 0) {
                maximum = point.x;
            }
        }
        if (maximum == null || minimum == null) {
            return ZERO;
        }
        return maximum.subtract(minimum);
    }

    public BigDecimal findHeight() {
        BigDecimal minimum = null, maximum = null;
        for (final OrderedPair<BigDecimal> point: points) {
            if (minimum == null || minimum.compareTo(point.y) > 0) {
                minimum = point.y;
            }
            if (maximum == null || maximum.compareTo(point.y) < 0) {
                maximum = point.y;
            }
        }
        if (maximum == null || minimum == null) {
            return ZERO;
        }
        return maximum.subtract(minimum);
    }

    public Path translate(final OrderedPair<BigDecimal> delta) {
        points.forEach((final OrderedPair<BigDecimal> point) -> {
            point.x = point.x.add(delta.x);
            point.y = point.y.add(delta.y);
        });
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder pointsBuilder = new StringBuilder();
        for (final OrderedPair<BigDecimal> point: points) {
            pointsBuilder.append("(").append(point.x).append(", ").append(point.y).append(") -> ");
        }
        if (pointsBuilder.length() > 0) {
            pointsBuilder.delete(pointsBuilder.length() - 4, pointsBuilder.length());
        }
        return String.format("Path[%s]", pointsBuilder.toString());
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
