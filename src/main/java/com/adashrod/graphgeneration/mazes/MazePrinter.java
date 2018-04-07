package com.adashrod.graphgeneration.mazes;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Objects;

/**
 * Created by aaron on 2018-04-05.
 */
public class MazePrinter {
    private final Maze maze;
    private final MazeWallModel mazeWallModel;

    public MazePrinter(final Maze maze) {
        this.maze = maze;
        this.mazeWallModel = null;
    }

    public MazePrinter(final MazeWallModel mazeWallModel) {
        this.maze = null;
        this.mazeWallModel = mazeWallModel;
    }

    void printSpaceDetail(final PrintStream printStream) {
        if (maze == null) {
            throw new IllegalStateException("maze can't be null");
        }
        final PrintStream o = printStream != null ? printStream : System.out;
        for (int y = 0; y < maze.getNumRows(); y++) {
            for (int x = 0; x < maze.getNumCols(); x++) {
                o.print(maze.getGrid()[y][x]);
                o.print("\t");
            }
            o.println();
        }
    }

    void printSpaceDetail() {
        printSpaceDetail(System.out);
    }

    public void printAsciiArt(final PrintStream printStream) {
        if (maze == null) {
            throw new IllegalStateException("maze can't be null");
        }
        final PrintStream o = printStream != null ? printStream : System.out;
        o.print("_");
        for (int x = 0; x < maze.getNumCols(); x++) {
            final Space space = maze.getGrid()[0][x];
            o.print(space.northOpen ? " " : "_");
            final Space next = x + 1 < maze.getNumCols() ? maze.getGrid()[0][x + 1] : null;
            final boolean nextNorthOpen = next == null || next.northOpen;
            o.print(space.northOpen && nextNorthOpen ? " " : "_");
        }
        o.println();
        for (int y = 0; y < maze.getNumRows(); y++) {
            // pre-phase: print left wall or opening at start of row
            o.print(maze.getGrid()[y][0].westOpen ? " " : "|");

            for (int x = 0; x < maze.getNumCols(); x++) {
                final Space s = maze.getGrid()[y][x];
                final boolean rightWall = x + 1 == maze.getNumCols();

                // phase 1: the space itself
                o.print(s.southOpen ? " " : "_");
                // phase 2: the wall to the right of the space (interstitial)
                final Space next = rightWall ? null : maze.getGrid()[y][x + 1];
                final boolean nextSouthOpen = next == null || next.southOpen;
                if (!s.eastOpen) {
                    o.print("|");
                } else if (s.southOpen && nextSouthOpen) {
                    o.print(" ");
                } else {
                    o.print("_");
                }
            }
            o.println();
        }
    }

    public void printAsciiArt() {
        printAsciiArt(System.out);
    }

    public void printTestSvg(final String name) {
        if (mazeWallModel == null) {
            throw new IllegalStateException("maze can't be null");
        }
        try (final FileWriter fileWriter = new FileWriter(name)) {
            final InputStream headerStream = getClass().getResourceAsStream("/header.svg");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(headerStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileWriter.append(line).append("\n");
            }
            fileWriter.append("<g ")
                .append("inkscape:label=\"Layer 1\" ")
                .append("inkscape:groupmode=\"layer\" ")
                .append("id=\"layer1\">");

            for (final MazeWallModel.Wall wall: mazeWallModel.walls) {
                final boolean vertical = Objects.equals(wall.start.x, wall.end.x);
                final double addX = vertical ? 4 : 0;
                final double addY = vertical ? 0 : 4;
                fileWriter.append("<rect style=\"stroke:#000000;fill:none\" ")
                    .append("x=\"").append(Double.toString(wall.start.x - addX)).append("\" ")
                    .append("y=\"").append(Double.toString(wall.start.y - addY)).append("\" ")
                    .append("width=\"").append(vertical ? "4" : Double.toString(Math.abs(wall.end.x - wall.start.x) - 4)).append("\" ")
                    .append("height=\"").append(vertical ? Double.toString(Math.abs(wall.end.y - wall.start.y) - 4) : "4").append("\" ")
                    .append("/>");
            }

            fileWriter.append("</g>\n")
                .append("</svg>\n");
            fileWriter.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
