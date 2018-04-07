package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.common.OrderedPair;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.NORTH;
import static com.adashrod.graphgeneration.mazes.Direction.SOUTH;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;

/**
 * An instance of MazeModelGenerator can be used to create a {@link MazeWallModel} from a {@link Maze}
 * @author adashrod@gmail.com
 */
public class MazeModelGenerator {
    private final Maze maze;
    private final double interval = 20;
    private boolean favorEwWalls;

    public MazeModelGenerator(final Maze maze) {
        this.maze = maze;
    }

    /**
     * @see MazeModelGenerator#isFavorEwWalls()
     * @param favorEwWalls true for east-west, false for north-south
     * @return this
     */
    public MazeModelGenerator setFavorEw(final boolean favorEwWalls) {
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

    public MazeWallModel generate() {
        return createWalls();
    }

    private MazeWallModel createWalls() {
        final MazeWallModel mazeWallModel = new MazeWallModel();

        final int width = maze.getNumCols(), height = maze.getNumRows(), lastCol = width - 1, lastRow = height - 1;
        if (favorEwWalls) {
            // make north walls of the rows
            for (int y = 0; y < height; y++) {
                makeWallsForLane(mazeWallModel, y, width, false, NORTH, null, false);
            }
            // south wall of final row
            makeWallsForLane(mazeWallModel, lastRow, width, false, SOUTH, null, true);
            // make west walls of columns
            for (int x = 0; x < width; x++) {
                makeWallsForLane(mazeWallModel, x, height, true, WEST, SOUTH, false);
            }
            // east wall of final column
            makeWallsForLane(mazeWallModel, lastCol, height, true, EAST, null, true);
        } else {
            // make west walls of columns
            for (int x = 0; x < width; x++) {
                makeWallsForLane(mazeWallModel, x, height, true, WEST, null, false);
            }
            // east wall of final column
            makeWallsForLane(mazeWallModel, lastCol, height, true, EAST, null, true);
            // make north walls of the rows
            for (int y = 0; y < height; y++) {
                makeWallsForLane(mazeWallModel, y, width, false, NORTH, EAST, false);
            }
            // south wall of final row
            makeWallsForLane(mazeWallModel, lastRow, width, false, SOUTH, null, true);
        }
        return mazeWallModel;
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
     * @param mazeWallModel              the model walls are being added to
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
    private void makeWallsForLane(final MazeWallModel mazeWallModel, final int majorTraversalIndex,
            final int minorTraversalMax, final boolean xMajor, final Direction continuationCheckDirection,
            final Direction overlapCheckDirection, final boolean isFinalWall) {
        for (int i = 0; i < minorTraversalMax; i++) {
            final int y = xMajor ? i : majorTraversalIndex;
            final int x = xMajor ? majorTraversalIndex : i;
            final Space currentSpace = maze.getGrid()[y][x];
            if (!currentSpace.isOpen(continuationCheckDirection)) {
                int length, additive = 0;
                for (length = 1; maze.isInBounds(xMajor ? x : x + length, xMajor ? y + length : y); length++) {
                    // wall continuation check
                    final Space nextSpace = maze.getGrid()[xMajor ? y + length : y][xMajor ? x : x + length];
                    if (nextSpace.isOpen(continuationCheckDirection)) {
                        break;
                    }
                    // wall overlap check
                    if (overlapCheckDirection != null) {
                        final int sameLaneX = xMajor ? x     : x + length - 1, sameLaneY = xMajor ? y + length - 1 : y;
                        final int prevLaneX = xMajor ? x - 1 : x + length - 1, prevLaneY = xMajor ? y + length - 1 : y - 1;
                        final Space spaceInSameLane = maze.getGrid()[sameLaneY][sameLaneX];
                        final Space spaceInPreviousLane = maze.isInBounds(prevLaneX, prevLaneY) ? maze.getGrid()[prevLaneY][prevLaneX] : null;
                        if (spaceInPreviousLane != null &&
                                !spaceInPreviousLane.isOpen(overlapCheckDirection) && !spaceInSameLane.isOpen(overlapCheckDirection)) {
                            // i += length puts i just past the wall that's blocked by a perpendicular one; -1 is needed
                            // so that the next loop iter still checks that space after i++ happens
                            additive = -1;
                            break;
                        }
                    }
                }
                final double startX, startY, endX, endY;
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
                final OrderedPair<Double> start = new OrderedPair<>(startX * interval, startY * interval),
                    end = new OrderedPair<>(endX * interval, endY * interval);
                mazeWallModel.addWall(new MazeWallModel.Wall(start, end));
                i += length + additive;
            }
        }
    }
}
