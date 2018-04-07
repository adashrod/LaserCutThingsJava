package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.adashrod.graphgeneration.mazes.Direction.determineDirection;

/**
 * Created by aaron on 2018-04-04.
 */
public class KruskalsAlgorithm extends MazeGenerator {
    private Tree[][] parallelMatrix;
    private final List<Edge> edges = new ArrayList<>();

    @Override
    public void buildPaths(final Maze maze) {
        parallelMatrix = new Tree[maze.getNumRows()][maze.getNumCols()];
        for (int y = 0; y < maze.getNumRows(); y++) {
            for (int x = 0; x < maze.getNumCols(); x++) {
                parallelMatrix[y][x] = new Tree();
                if (x + 1 < maze.getNumCols()) {
                    edges.add(new Edge(new OrderedPair<>(x, y), new OrderedPair<>(x + 1, y)));
                }
                if (y + 1 < maze.getNumRows()) {
                    edges.add(new Edge(new OrderedPair<>(x, y), new OrderedPair<>(x,     y + 1)));
                }
            }
        }
        Collections.shuffle(edges, rng);

        for (final Edge e: edges) {
            final OrderedPair<Integer> s1 = e.a;
            final OrderedPair<Integer> s2 = e.b;
            final Tree tree1 = parallelMatrix[s1.y][s1.x];
            final Tree tree2 = parallelMatrix[s2.y][s2.x];
            if (!tree1.isConnectedTo(tree2)) {
                tree1.merge(tree2);
                final Direction oneToTwo = determineDirection(s1, s2);
                maze.getGrid()[s1.y][s1.x].openWall(oneToTwo);
                maze.getGrid()[s2.y][s2.x].openWall(oneToTwo.opposite());
            }
        }
    }

    static class Tree {
        Tree parent = this;

        Tree getRoot() {
            return parent == this ? parent : parent.getRoot();
        }

        boolean isConnectedTo(final Tree otherTree) {
            return getRoot() == otherTree.getRoot();
        }

        void merge(final Tree otherTree) {
            otherTree.getRoot().parent = this;
        }
    }

    static class Edge {
        final OrderedPair<Integer> a;
        final OrderedPair<Integer> b;

        Edge(final OrderedPair<Integer> a, final OrderedPair<Integer> b) {
            this.a = a;
            this.b = b;
        }
    }
}
