package com.adashrod.lasercutthings.mazes.factories;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.Direction;
import com.adashrod.lasercutthings.mazes.models.Maze;
import com.adashrod.lasercutthings.mazes.Space;
import com.adashrod.lasercutthings.mazes.models.LinearWallModel;

import static com.adashrod.lasercutthings.mazes.Direction.EAST;
import static com.adashrod.lasercutthings.mazes.Direction.NORTH;
import static com.adashrod.lasercutthings.mazes.Direction.SOUTH;
import static com.adashrod.lasercutthings.mazes.Direction.WEST;

/**
 * An instance of LinearWallModelGenerator can be used to create a {@link LinearWallModel} from a {@link Maze}
 * @author adashrod@gmail.com
 */
public class LinearWallModelGenerator {
    private final Maze maze;
    private boolean favorEwWalls;

    public LinearWallModelGenerator(final Maze maze) {
        this.maze = maze;
    }

    /**
     * @see LinearWallModelGenerator#isFavorEwWalls()
     * @param favorEwWalls true for east-west, false for north-south
     * @return this
     */
    public LinearWallModelGenerator setFavorEw(final boolean favorEwWalls) {
        this.favorEwWalls = favorEwWalls;
        return this;
    }

    /**
     * if favorEwWalls is true, the generator will build east-west (horizontal) walls first, and north-south (vertical)
     * walls second; if false, vice-versa. The important difference is which wall will permeate through a plus-shaped
     * intersection of two long walls. The second wall built will be split at the intersection into 2 parts.
     * @return true for east-west, false for north-south
     */
    public boolean isFavorEwWalls() {
        return favorEwWalls;
    }

    public LinearWallModel generate() {
        final int width = maze.getNumCols(), height = maze.getNumRows(), lastCol = width - 1, lastRow = height - 1;
        final LinearWallModel linearWallModel = new LinearWallModel(width, height, favorEwWalls);

        if (favorEwWalls) {
            // make north walls of the rows
            for (int y = 0; y < height; y++) {
                makeWallsForLane(linearWallModel, y, width, false, NORTH, null, false);
            }
            // south wall of final row
            makeWallsForLane(linearWallModel, lastRow, width, false, SOUTH, null, true);
            // make west walls of columns
            for (int x = 0; x < width; x++) {
                makeWallsForLane(linearWallModel, x, height, true, WEST, SOUTH, false);
            }
            // east wall of final column
            makeWallsForLane(linearWallModel, lastCol, height, true, EAST, SOUTH, true);
        } else {
            // make west walls of columns
            for (int x = 0; x < width; x++) {
                makeWallsForLane(linearWallModel, x, height, true, WEST, null, false);
            }
            // east wall of final column
            makeWallsForLane(linearWallModel, lastCol, height, true, EAST, null, true);
            // make north walls of the rows
            for (int y = 0; y < height; y++) {
                makeWallsForLane(linearWallModel, y, width, false, NORTH, EAST, false);
            }
            // south wall of final row
            makeWallsForLane(linearWallModel, lastRow, width, false, SOUTH, EAST, true);
        }
        return linearWallModel;
    }

    /**
     * traverses a lane (row or column) of the maze, making as many walls that are needed for that lane, consolidating
     * adjacent walls when possible.
     * e.g. a row like ___ __ (3 spaces with walls, 1 empty, 2 more with walls)
     * This would make two walls; one 3 spaces long and the other 2 spaces long
     * e.g. when an overlapCheckDirection is used: ___|_
     *                                                |
     *     There's a wall 4 spaces long, and a perpendicular wall. If building vertical walls first, this horizontal
     *     wall would be split into 2 separate horizontal walls: one length 3, and one length 1, end-to-end, but separate
     *     so that the perpendicular wall doesn't overlap. If doing horizontal walls first, this would result in one
     *     horizontal wall 4 spaces long and two separate vertical walls.
     * @param linearWallModel            the model walls are being added to
     * @param majorTraversalIndex        the index of the lane being traversed
     * @param minorTraversalMax          number of spaces in the lane
     * @param xMajor                     true if doing an x-major (column-major) traversal
     * @param continuationCheckDirection direction to check that the wall continues, e.g. if doing a row-major traversal
     *                                   (moving east), one should check that there are walls on the north or south sides
     *                                   of the spaces to see how far they continue
     * @param overlapCheckDirection      direction to check for perpendicular walls, e.g. if doing a row-major traversal
     *                                   (moving east), one should check that there are perpendicular walls to the east
     *                                   which would determine the end of the current wall
     * @param isFinalWall                true if this is the last row/column being checked, used for determining
     *                                   coordinates since n rows means n+1 rows of horizontal walls
     */
    private void makeWallsForLane(final LinearWallModel linearWallModel, final int majorTraversalIndex,
            final int minorTraversalMax, final boolean xMajor, final Direction continuationCheckDirection,
            final Direction overlapCheckDirection, final boolean isFinalWall) {
        for (int i = 0; i < minorTraversalMax; i++) {
            final int y = xMajor ? i : majorTraversalIndex;
            final int x = xMajor ? majorTraversalIndex : i;
            final Space currentSpace = maze.getGrid()[y][x];
            if (!currentSpace.isOpen(continuationCheckDirection)) {
                final OrderedPair<Integer> lengthAndAdditive = calculateWallLength(xMajor, continuationCheckDirection,
                    overlapCheckDirection, isFinalWall, x, y);
                final int length = lengthAndAdditive.x, additive = lengthAndAdditive.y;
                linearWallModel.addWall(createWallHelper(xMajor, isFinalWall, x, y, length));
                i += length + additive;
            }
        }
    }

    private OrderedPair<Integer> calculateWallLength(final boolean xMajor, final Direction continuationCheckDirection,
        final Direction overlapCheckDirection, final boolean isFinalWall, final int x, final int y) {
        int length, additive = 0;
        for (length = 1; maze.isInBounds(xMajor ? x : x + length, xMajor ? y + length : y); length++) {
            // wall continuation check
            final Space nextSpace = findNextSpace(xMajor, x, y, length);
            if (nextSpace.isOpen(continuationCheckDirection)) {
                break;
            }
            // wall overlap check
            if (overlapCheckDirection != null) {
                final int sameLaneX = findSameLaneX(xMajor, x, length), sameLaneY = findSameLaneY(xMajor, y, length);
                final int prevLaneX = findPrevLaneX(xMajor, x, length), prevLaneY = findPrevLaneY(xMajor, y, length);
                final Space spaceInSameLane = maze.getGrid()[sameLaneY][sameLaneX];
                final Space spaceInPrevLane = findSpaceInPrevLane(prevLaneX, prevLaneY);
                // 1st condition: check for perpendicular wall in same lane; 2nd: check for perpendicular wall
                // in prev lane, but not for final row because we don't care about prev lane when doing the outer
                // check
                if (!spaceInSameLane.isOpen(overlapCheckDirection) ||
                    (!isFinalWall && spaceInPrevLane != null && !spaceInPrevLane.isOpen(overlapCheckDirection))) {
                    // i += length puts i just past the wall that's blocked by a perpendicular one; -1 is needed
                    // so that the next loop iter still checks that space after i++ happens
                    additive = -1;
                    break;
                }
            }
        }
        return new OrderedPair<>(length, additive);
    }

    private Space findNextSpace(final boolean xMajor, final int x, final int y, final int length) {
        return maze.getGrid()[xMajor ? y + length : y][xMajor ? x : x + length];
    }

    private int findSameLaneX(final boolean xMajor, final int x, final int length) {
        return xMajor ? x : x + length - 1;
    }

    private int findSameLaneY(final boolean xMajor, final int y, final int length) {
        return xMajor ? y + length - 1 : y;
    }

    private int findPrevLaneX(final boolean xMajor, final int x, final int length) {
        return xMajor ? x - 1 : x + length - 1;
    }

    private int findPrevLaneY(final boolean xMajor, final int y, final int length) {
        return xMajor ? y + length - 1 : y - 1;
    }

    private Space findSpaceInPrevLane(final int prevLaneX, final int prevLaneY) {
        return maze.isInBounds(prevLaneX, prevLaneY) ? maze.getGrid()[prevLaneY][prevLaneX] : null;
    }

    private LinearWallModel.Wall createWallHelper(final boolean xMajor, final boolean isFinalWall,
            final int x, final int y, final int length) {
        final int startX, startY, endX, endY;
        if (xMajor) {
            startY = y;
            endY = y + length;
            if (isFinalWall) {
                startX = x + 1;
                endX = x + 1;
            } else {
                startX = x;
                endX = x;
            }
        } else {
            startX = x;
            endX = x + length;
            if (isFinalWall) {
                startY = y + 1;
                endY = y + 1;
            } else {
                startY = y;
                endY = y;
            }
        }
        final OrderedPair<Integer> start = new OrderedPair<>(startX, startY), end = new OrderedPair<>(endX, endY);
        return new LinearWallModel.Wall(start, end);
    }
}
