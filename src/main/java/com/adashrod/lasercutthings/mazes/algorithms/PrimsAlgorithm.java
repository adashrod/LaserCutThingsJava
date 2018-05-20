package com.adashrod.lasercutthings.mazes.algorithms;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.Direction;
import com.adashrod.lasercutthings.mazes.Space;
import com.adashrod.lasercutthings.mazes.models.Maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.adashrod.lasercutthings.mazes.Direction.EAST;
import static com.adashrod.lasercutthings.mazes.Direction.NORTH;
import static com.adashrod.lasercutthings.mazes.Direction.SOUTH;
import static com.adashrod.lasercutthings.mazes.Direction.WEST;
import static com.adashrod.lasercutthings.mazes.Direction.determineDirection;

/**
 * An implementation of https://en.wikipedia.org/wiki/Prim%27s_algorithm for generating random 2D mazes with square
 * spaces
 * @author adashrod@gmail.com
 */
public class PrimsAlgorithm extends MazeGenerator {
    private Maze maze;
    private final List<OrderedPair<Integer>> nextSpaces = new ArrayList<>();
    private final Collection<OrderedPair<Integer>> onPath = new HashSet<>();
    private final Collection<OrderedPair<Integer>> exploringNext = new HashSet<>();
    private final List<OrderedPair<Integer>> deltas = Arrays.asList(
        new OrderedPair<>(0, -1), new OrderedPair<>(1, 0), new OrderedPair<>(0, 1), new OrderedPair<>(-1, 0));

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
        if (maze.isInBounds(x, y) && isUnexplored(x, y)) {
            exploringNext.add(new OrderedPair<>(x, y));
            nextSpaces.add(new OrderedPair<>(x, y));
        }
    }

    private void markOnPathAndAddUnexploredNeighborsToNext(final int x, final int y) {
        onPath.add(new OrderedPair<>(x, y));
        deltas.forEach((final OrderedPair<Integer> delta) -> {
            addToNextIfUnexplored(x + delta.x, y + delta.y);
        });
    }

    private List<OrderedPair<Integer>> findOnPathNeighbors(final int x, final int y) {
        final List<OrderedPair<Integer>> n = new ArrayList<>();
        deltas.forEach((final OrderedPair<Integer> delta) -> {
            if (maze.isInBounds(x + delta.x, y + delta.y) && onPath.contains(new OrderedPair<>(x + delta.x, y + delta.y))) {
                n.add(new OrderedPair<>(x + delta.x, y + delta.y));
            }
        });
        return n;
    }

    private boolean isUnexplored(final int x, final int y) {
        final Space space = maze.getGrid()[y][x];
        return !space.isOpen(NORTH) && !space.isOpen(EAST) && !space.isOpen(SOUTH) && !space.isOpen(WEST) &&
            !exploringNext.contains(new OrderedPair<>(x, y));
    }
}
