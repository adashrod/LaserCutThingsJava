package com.adashrod.graphgeneration.mazes.factories;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
 * todo
 * @author adashrod@gmail.com
 */
class SheetWallTilingOptimizer {
    private final SheetWallModel sheetWallModel;
    private final BigDecimal separationSpace, maxWidth, maxHeight;
    private OrderedPair<BigDecimal> cursor = new OrderedPair<>(ZERO, ZERO);
    private BigDecimal beginningOfLineX;
    private BigDecimal currentMaxRowWidth;

    SheetWallTilingOptimizer(final SheetWallModel sheetWallModel, final BigDecimal separationSpace,
            final BigDecimal maxWidth, final BigDecimal maxHeight) {
        this.sheetWallModel = sheetWallModel;
        this.separationSpace = separationSpace;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    void optimize() {
        final BigDecimal floorWidth = sheetWallModel.floorOutline.findWidth(),
            floorHeight = sheetWallModel.floorOutline.findHeight(),
            wallHeight = sheetWallModel.walls.get(0).findHeight();
        cursor = new OrderedPair<>(ZERO, floorHeight);
        final List<SheetWallModel.Shape> sortedWalls = new ArrayList<>(sheetWallModel.walls);
        sheetWallModel.walls.clear();
        sortedWalls.sort(Comparator.comparing(SheetWallModel.Shape::findWidth).reversed());
        final Deque<SheetWallModel.Shape> shapesDeque = new LinkedList<>(sortedWalls);
        beginningOfLineX = ZERO;
        currentMaxRowWidth = floorWidth;
        cursor.y = sheetWallModel.floorOutline.findHeight().add(separationSpace);
        while (!shapesDeque.isEmpty()) {
            if (fitsInNewRow(wallHeight)) {
                // add to new row in current column
                final SheetWallModel.Shape longWall = shapesDeque.pollFirst();
                addToCurrentRow(longWall);
                // so that we don't overwrite cmrw when it's already been set to the floor width on the first iteration
                if (currentMaxRowWidth == null) {
                    currentMaxRowWidth = longWall.findWidth();
                    if (cursor.x.add(currentMaxRowWidth).compareTo(maxWidth) > 0) {
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
                final SheetWallModel.Shape shortWall = shapesDeque.peekLast();
                if (fitsInCurrentRow(shortWall)) {
                    addToCurrentRow(shapesDeque.pollLast());
                } else {
                    cursor = new OrderedPair<>(beginningOfLineX, cursor.y.add(wallHeight).add(separationSpace));
                    break;
                }
            }
        }
    }

    private void addToCurrentRow(final SheetWallModel.Shape wall) {
        wall.translate(cursor);
        sheetWallModel.walls.add(wall);
        cursor.x = cursor.x.add(wall.findWidth()).add(separationSpace);
    }

    private boolean fitsInNewRow(final BigDecimal wallHeight) {
        return cursor.y.add(wallHeight).compareTo(maxHeight) <= 0;
    }

    private boolean fitsInCurrentRow(final SheetWallModel.Shape wall) {
        return cursor.x.subtract(beginningOfLineX).add(wall.findWidth()).compareTo(currentMaxRowWidth) <= 0;
    }
}
