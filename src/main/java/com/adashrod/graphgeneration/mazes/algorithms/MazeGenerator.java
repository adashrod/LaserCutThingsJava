package com.adashrod.graphgeneration.mazes.algorithms;

import com.adashrod.graphgeneration.mazes.models.Maze;

import java.util.Random;

/**
 * Subclasses can use any maze-generation algorithm to build the paths of a maze
 * @author adashrod@gmail.com
 */
public abstract class MazeGenerator {
    /**
     * Used for making random decisions in building mazes. The seed can be set for reproducibility
     */
    protected final Random rng = new Random();

    /**
     * This is where the random mazes get generated. Algorithms should set wall properties on the spaces in the supplied
     * maze as needed
     * @param maze the maze to randomize
     */
    public abstract void buildPaths(Maze maze);

    /**
     * Sets the random seed of the random number generator for reproducibility
     * @param seed
     * @return this
     */
    public MazeGenerator setSeed(final long seed) {
        rng.setSeed(seed);
        return this;
    }
}
