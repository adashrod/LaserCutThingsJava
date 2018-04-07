package com.adashrod.graphgeneration.mazes;

import java.util.Random;

/**
 * Created by arodriguez on 2018-04-04.
 */
public abstract class MazeGenerator {
    protected final Random rng = new Random();

    public abstract void buildPaths(Maze maze);

    public MazeGenerator setSeed(final long seed) {
        rng.setSeed(seed);
        return this;
    }
}
