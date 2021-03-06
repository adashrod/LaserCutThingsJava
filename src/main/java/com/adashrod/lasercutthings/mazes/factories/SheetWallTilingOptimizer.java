package com.adashrod.lasercutthings.mazes.factories;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.models.Shape;
import com.adashrod.lasercutthings.mazes.models.SheetWallModel;
import com.adashrod.lasercutthings.mazes.models.VectorNumber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
 * This class has an optimization function that tiles the walls in the sheet so that they're
 * - tiled in a way that fits within the max width and max height
 * - tiled in a way that wastes a minimal amount of space
 * @author adashrod@gmail.com
 */
class SheetWallTilingOptimizer {
    private final SheetWallModel sheetWallModel;
    private final BigDecimal separationSpace;
    private final BigDecimal maxWidth;
    private final BigDecimal maxHeight;
    private final BigDecimal wallHeight;
    private OrderedPair<BigDecimal> cursor = new OrderedPair<>(ZERO, ZERO);
    private BigDecimal beginningOfLineX;
    private BigDecimal currentMaxRowWidth;

    SheetWallTilingOptimizer(final SheetWallModel sheetWallModel, final BigDecimal separationSpace,
            final BigDecimal maxWidth, final BigDecimal maxHeight, final BigDecimal wallHeight) {
        this.sheetWallModel = sheetWallModel;
        this.separationSpace = separationSpace;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.wallHeight = wallHeight;
    }

    // todo: could make this even more efficient by doing rows instead of columns after the first column
    void optimize() {
        final BigDecimal floorWidth = sheetWallModel.floorOutline.findWidth(),
            floorHeight = sheetWallModel.floorOutline.findHeight(),
            wallHeight = sheetWallModel.walls.get(0).findHeight();
        cursor = new OrderedPair<>(ZERO, floorHeight);
        final List<Shape> sortedWalls = new ArrayList<>(sheetWallModel.walls);
        sheetWallModel.walls.clear();
        sortedWalls.sort(Comparator.comparing(Shape::findWidth).reversed());
        final Deque<Shape> shapesDeque = new LinkedList<>(sortedWalls);
        beginningOfLineX = ZERO;
        currentMaxRowWidth = floorWidth;
        cursor.y = sheetWallModel.floorOutline.findHeight().add(separationSpace);
        while (!shapesDeque.isEmpty()) {
            if (fitsInNewRow(wallHeight)) {
                // add to new row in current column
                final Shape longWall = shapesDeque.pollFirst();
                addToCurrentRow(longWall);
                // so that we don't overwrite cmrw when it's already been set to the floor width on the first iteration
                if (currentMaxRowWidth == null) {
                    currentMaxRowWidth = longWall.findWidth();
                    if (cursor.x.compareTo(maxWidth) > 0) {
                        sheetWallModel.outOfBounds = true;
                    }
                }
            } else {
                // end of column reached, move right to new column
                cursor = new OrderedPair<>(cursor.x.add(currentMaxRowWidth).add(separationSpace), ZERO);
                beginningOfLineX = cursor.x;
                currentMaxRowWidth = null;
                continue;
            }
            while (!shapesDeque.isEmpty()) {
                final Shape shortWall = shapesDeque.peekLast();
                if (fitsInCurrentRow(shortWall)) {
                    addToCurrentRow(shapesDeque.pollLast());
                } else {
                    cursor = new OrderedPair<>(beginningOfLineX, cursor.y.add(wallHeight).add(separationSpace));
                    break;
                }
            }
        }
    }

    private void addToCurrentRow(final Shape wall) {
        wall.translate(cursor);
        final VectorNumber wallLabel = sheetWallModel.wallLabels.get(wall);
        final BigDecimal half = new BigDecimal(".5");
        wallLabel.translate(new OrderedPair<>(
            cursor.x.add(wall.findWidth().multiply(half)).subtract(wallLabel.width.multiply(half)),
            cursor.y.add(wallHeight.multiply(half)).subtract(wallLabel.height.multiply(half))
        ));
        sheetWallModel.walls.add(wall);
        cursor.x = cursor.x.add(wall.findWidth()).add(separationSpace);
    }

    private boolean fitsInNewRow(final BigDecimal wallHeight) {
        return cursor.y.add(wallHeight).compareTo(maxHeight) <= 0;
    }

    private boolean fitsInCurrentRow(final Shape wall) {
        return cursor.x.subtract(beginningOfLineX).add(wall.findWidth()).compareTo(currentMaxRowWidth) <= 0;
    }
}
