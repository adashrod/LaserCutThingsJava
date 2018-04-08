package com.adashrod.graphgeneration.mazes.algorithms;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.Direction;
import com.adashrod.graphgeneration.mazes.models.Maze;

import java.util.ArrayList;
import java.util.List;

import static com.adashrod.graphgeneration.mazes.Direction.determineDirection;

/**
 * An implementation of https://en.wikipedia.org/wiki/Prim%27s_algorithm for generating random 2D mazes with square
 * spaces
 * @author adashrod@gmail.com
 */
public class PrimsAlgorithm extends MazeGenerator {
    private Maze maze;
    private final List<OrderedPair<Integer>> nextSpaces = new ArrayList<>();

    @Override
    public void buildPaths(final Maze maze) {
        this.maze = maze;
        markOnPathAndAddUnexploredNeighborsToNext(rng.nextInt(maze.getNumCols()), rng.nextInt(maze.getNumRows()));
        while (!nextSpaces.isEmpty()) {
            final OrderedPair<Integer> removed = nextSpaces.remove(rng.nextInt(nextSpaces.size()));
            final List<OrderedPair<Integer>> neighbors = findOnPathNeighbors(removed.x, removed.y);
            final OrderedPair<Integer> randNeighbor = neighbors.get(rng.nextInt(neighbors.size()));

            final Direction direction = determineDirection(removed, randNeighbor);
            maze.getGrid()[removed.y][removed.x].openWall(direction);
            maze.getGrid()[randNeighbor.y][randNeighbor.x].openWall(direction.opposite());

            markOnPathAndAddUnexploredNeighborsToNext(removed.x, removed.y);
        }
    }

    private void addToNextIfUnexplored(final int x, final int y) {
        if (maze.isInBounds(x, y) && maze.getGrid()[y][x].isUnexplored()) {
            maze.getGrid()[y][x].exploringNext = true;
            nextSpaces.add(new OrderedPair<>(x, y));
        }
    }

    private void markOnPathAndAddUnexploredNeighborsToNext(final int x, final int y) {
        maze.getGrid()[y][x].onPath = true;
        addToNextIfUnexplored(x - 1, y);
        addToNextIfUnexplored(x + 1, y);
        addToNextIfUnexplored(x,     y - 1);
        addToNextIfUnexplored(x,     y + 1);
    }

    private List<OrderedPair<Integer>> findOnPathNeighbors(final int x, final int y) {
        final List<OrderedPair<Integer>> n = new ArrayList<>();
        if (maze.isInBounds(x - 1, y)     && maze.getGrid()[y    ][x - 1].onPath) {
            n.add(new OrderedPair<>(x - 1, y));
        }
        if (maze.isInBounds(x + 1, y)     && maze.getGrid()[y    ][x + 1].onPath) {
            n.add(new OrderedPair<>(x + 1, y));
        }
        if (maze.isInBounds(x,     y - 1) && maze.getGrid()[y - 1][x    ].onPath) {
            n.add(new OrderedPair<>(x,     y - 1));
        }
        if (maze.isInBounds(x,     y + 1) && maze.getGrid()[y + 1][x    ].onPath) {
            n.add(new OrderedPair<>(x,     y + 1));
        }
        return n;
    }
}
