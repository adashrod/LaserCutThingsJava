package com.adashrod.lasercutthings.mazes.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This model class is a model of the walls and floor that compose a maze with the intention that each piece (wall or
 * floor) is of a constant thickness (the thickness of the physical sheet that will be used) and the walls will be
 * rotated upward and attached to the floor.
 * @author adashrod@gmail.com
 */
public class SheetWallModel {
    public final Shape floorNotches = new Shape();
    public final Shape floorOutline = new Shape();
    public final List<Shape> walls = new ArrayList<>();
    public final List<VectorNumber> floorNumbers = new ArrayList<>();
    public final Map<Shape, VectorNumber> wallLabels = new HashMap<>();
    public boolean outOfBounds;

    public SheetWallModel addShape(final Shape shape) {
        walls.add(shape);
        return this;
    }
}
