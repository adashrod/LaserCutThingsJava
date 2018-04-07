package com.adashrod.graphgeneration.mazes;

/**
 * This maze class is a representation of a maze based on a grid of squares. Each square knows about the four walls
 * surrounding it.
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

    public boolean isInBounds(final int x, final int y) {
        return x >= 0 && x < numCols && y >= 0 && y < numRows;
    }

    public void build(final MazeGenerator generatorAlgorithm) {
        initGrid();
        generatorAlgorithm.buildPaths(this);
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public Space[][] getGrid() {
        return grid;
    }
}
