package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This model class is a model of the walls and floor that compose a maze with the intention that each piece (wall or
 * floor) is of a constant thickness (the thickness of the physical sheet that will be used) and the walls will be
 * rotated upward and attached to the floor.
 * @author adashrod@gmail.com
 */
public class SheetWallModel {
    private final Collection<Shape> shapes = new ArrayList<>();

    public SheetWallModel addShape(final Shape shape) {
        shapes.add(shape);
        return this;
    }

    public static class Shape {
        private final List<OrderedPair<Double>> points = new ArrayList<>();

        public Shape addPoint(final OrderedPair<Double> point) {
            points.add(point);
            return this;
        }
    }
}
