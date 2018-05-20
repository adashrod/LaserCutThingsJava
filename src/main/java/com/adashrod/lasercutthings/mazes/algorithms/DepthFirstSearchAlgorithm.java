package com.adashrod.lasercutthings.mazes.algorithms;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.Direction;
import com.adashrod.lasercutthings.mazes.models.Maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static com.adashrod.lasercutthings.mazes.Direction.determineDirection;

/**
 * An implementation of https://en.wikipedia.org/wiki/Depth-first_search
 * @author adashrod@gmail.com
 */
public class DepthFirstSearchAlgorithm extends MazeGenerator {
    private Maze maze;
    private final Collection<OrderedPair<Integer>> explored = new HashSet<>();
    private final List<OrderedPair<Integer>> deltas = Arrays.asList(
        new OrderedPair<>(0, -1), new OrderedPair<>(1, 0), new OrderedPair<>(0, 1), new OrderedPair<>(-1, 0));

    @Override
    public void buildPaths(final Maze maze) {
        this.maze = maze;
        final Deque<OrderedPair<Integer>> stack = new LinkedList<>();
        OrderedPair<Integer> current = new OrderedPair<>(rng.nextInt(maze.getNumCols()), rng.nextInt(maze.getNumRows()));
        explored.add(new OrderedPair<>(current.x, current.y));
        final int numSpaces = maze.getNumRows() * maze.getNumCols();
        while (explored.size() < numSpaces) {
            final List<OrderedPair<Integer>> neighbors = findUnexploredNeighbors(current.x, current.y);
            if (!neighbors.isEmpty()) {
                final OrderedPair<Integer> randomNeighbor = neighbors.get(rng.nextInt(neighbors.size()));
                stack.offerFirst(current);
                final Direction direction = determineDirection(current, randomNeighbor);
                maze.getGrid()[current.y][current.x].openWall(direction);
                maze.getGrid()[randomNeighbor.y][randomNeighbor.x].openWall(direction.opposite());
                current = randomNeighbor;
                explored.add(new OrderedPair<>(current.x, current.y));
            } else {
                if (stack.isEmpty()) {
                    break;
                }
                current = stack.pollFirst();
            }
        }
    }

    private List<OrderedPair<Integer>> findUnexploredNeighbors(final int x, final int y) {
        final List<OrderedPair<Integer>> neighbors = new ArrayList<>();
        deltas.forEach((final OrderedPair<Integer> delta) -> {
            if (maze.isInBounds(x + delta.x, y + delta.y) && !explored.contains(new OrderedPair<>(x + delta.x, y + delta.y))) {
                neighbors.add(new OrderedPair<>(x + delta.x, y + delta.y));
            }
        });
        return neighbors;
    }
}
