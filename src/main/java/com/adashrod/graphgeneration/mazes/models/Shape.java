package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
 * A shape is a collection of paths. It can have one or many paths that are each not connected to each other.
 */
public class Shape {
    public final List<Path> paths = new ArrayList<>();
    private BigDecimal cachedWidth, cachedHeight;

    public Shape() {}

    public Shape(final Path path) {
        paths.add(path);
    }

    public Shape addPath(final Path path) {
        paths.add(path);
        cachedWidth = cachedHeight = null;
        return this;
    }

    public Shape addShape(final Shape shape) {
        paths.addAll(shape.paths);
        cachedWidth = cachedHeight = null;
        return this;
    }

    public BigDecimal findWidth() {
        if (cachedWidth != null) {
            return cachedWidth;
        }
        BigDecimal minimum = ZERO, maximum = ZERO;
        for (final Path path: paths) {
            for (final OrderedPair<BigDecimal> point: path.points) {
                if (minimum == null || minimum.compareTo(point.x) > 0) {
                    minimum = point.x;
                }
                if (maximum == null || maximum.compareTo(point.x) < 0) {
                    maximum = point.x;
                }
            }
        }
        return cachedWidth = maximum.subtract(minimum);
    }

    public BigDecimal findHeight() {
        if (cachedHeight != null) {
            return cachedHeight;
        }
        BigDecimal minimum = ZERO, maximum = ZERO;
        for (final Path path: paths) {
            for (final OrderedPair<BigDecimal> point: path.points) {
                if (minimum == null || minimum.compareTo(point.y) > 0) {
                    minimum = point.y;
                }
                if (maximum == null || maximum.compareTo(point.y) < 0) {
                    maximum = point.y;
                }
            }
        }
        return cachedHeight = maximum.subtract(minimum);
    }

    public Shape translate(final OrderedPair<BigDecimal> delta) {
        paths.forEach(path -> path.translate(delta));
        return this;
    }

    /**
     * for now, at least, this assumes that the object is positioned at 0,0. If that's not the case, this will also end
     * up doing an unwanted translation
     * @param scaleFactor
     * @return
     */
    public Shape scale(final OrderedPair<BigDecimal> scaleFactor) {
        paths.forEach((final Path path) -> {
            path.points.forEach((final OrderedPair<BigDecimal> point) -> {
                point.x = point.x.multiply(scaleFactor.x);
                point.y = point.y.multiply(scaleFactor.y);
            });
        });
        return this;
    }

    public static Shape copy(final Shape shape) {
        final Shape copy = new Shape();
        shape.paths.forEach((final Path path) -> {
            // todo: implement Path#copy?
            final Path pathCopy = new Path();
            path.points.forEach((final OrderedPair<BigDecimal> point) -> {
                pathCopy.addPoint(new OrderedPair<>(point.x, point.y));
            });
            pathCopy.setClosed(path.isClosed);
            copy.addPath(pathCopy);
        });
        return copy;
    }
}
