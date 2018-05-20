package com.adashrod.lasercutthings.mazes.models;

import com.adashrod.lasercutthings.mazes.Space;
import com.adashrod.lasercutthings.mazes.algorithms.MazeGenerator;

/**
 * This maze class is a representation of a maze based on a grid of squares. Each square knows about the four walls
 * surrounding it.
 * @author adashrod@gmail.com
 */
public class Maze {
    private final int numCols;
    private final int numRows;
    private final Space[][] grid;

    public Maze(final int numVerticalLanes, final int numHorizontalLanes) {
        numCols = numVerticalLanes;
        numRows = numHorizontalLanes;
        grid = new Space[numRows][numCols];
        initGrid();
    }

    private void initGrid() {
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numCols; x++) {
                grid[y][x] = new Space();
            }
        }
    }

    /**
     * check if a space determined by the coordinates is inside the maze boundary
     * @param x
     * @param y
     * @return
     */
    public boolean isInBounds(final int x, final int y) {
        return x >= 0 && x < numCols && y >= 0 && y < numRows;
    }

    /**
     * Randomizes this maze using the supplied algorithm
     * @param generatorAlgorithm which algorithm to use
     */
    public void build(final MazeGenerator generatorAlgorithm) {
        initGrid();
        generatorAlgorithm.buildPaths(this);
    }

    /**
     * @return number of columns/width of the maze
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * @return number of rows/height of the maze
     */
    public int getNumRows() {
        return numRows;
    }

    public Space[][] getGrid() {
        return grid;
    }
}
