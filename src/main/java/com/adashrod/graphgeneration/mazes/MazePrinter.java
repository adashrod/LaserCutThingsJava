package com.adashrod.graphgeneration.mazes;

import com.adashrod.graphgeneration.mazes.models.CalibrationRectangle;
import com.adashrod.graphgeneration.mazes.models.LinearWallModel;
import com.adashrod.graphgeneration.mazes.models.Maze;
import com.adashrod.graphgeneration.mazes.models.Shape;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;
import com.adashrod.graphgeneration.mazes.models.VectorNumber;
import com.adashrod.graphgeneration.svg.Path;
import com.adashrod.graphgeneration.svg.Rect;
import com.adashrod.graphgeneration.svg.SvgElementGenerator;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * Utility for printing {@link Maze}s and {@link LinearWallModel}s in various forms
 * @author adashrod@gmail.com
 */
public class MazePrinter {
    private final Maze maze;
    private final LinearWallModel linearWallModel;
    public final SheetWallModel sheetWallModel;

    public int precision = 5;

    /**
     * @param maze the maze to print with this MazePrinter instance
     */
    public MazePrinter(final Maze maze) {
        this.maze = maze;
        this.linearWallModel = null;
        this.sheetWallModel = null;
    }

    /**
     * @param linearWallModel the maze to print with this MazePrinter instance
     */
    public MazePrinter(final LinearWallModel linearWallModel) {
        this.maze = null;
        this.linearWallModel = linearWallModel;
        this.sheetWallModel = null;
    }

    /**
     * @param sheetWallModel the maze to print with this MazePrinter instance
     */
    public MazePrinter(final SheetWallModel sheetWallModel) {
        this.maze = null;
        this.linearWallModel = null;
        this.sheetWallModel = sheetWallModel;
    }

    /**
     * for debugging, prints the detail of the {@link Space}s in the {@link this#maze}
     * @param printStream where to print to
     */
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

    /**
     * for debugging, prints the detail of the {@link Space}s in the maze to System.out
     */
    void printSpaceDetail() {
        printSpaceDetail(System.out);
    }

    /**
     * Prints the {@link this#maze} to printStream using '_', '|', and ' '
     * Each row in the maze will be represented in one line of output. The rows will have 2 chars printed for each space:
     * 1 will be a ' ' if there is no south wall or '_' if there is; 1 will be '|' if there is an east wall
     * @param printStream where to print to
     */
    public void printAsciiArt(final PrintStream printStream) {
        if (maze == null) {
            throw new IllegalStateException("maze can't be null");
        }
        final PrintStream o = printStream != null ? printStream : System.out;
        printAsciiArtHelperFirstRow(o);
        for (int y = 0; y < maze.getNumRows(); y++) {
            printAsciiArtHelperRow(o, y);
        }
    }

    private void printAsciiArtHelperFirstRow(final PrintStream o) {
        o.print("_");
        for (int x = 0; x < maze.getNumCols(); x++) {
            final Space space = maze.getGrid()[0][x];
            o.print(space.northOpen ? " " : "_");
            final Space next = x + 1 < maze.getNumCols() ? maze.getGrid()[0][x + 1] : null;
            final boolean nextNorthOpen = next == null || next.northOpen;
            o.print(space.northOpen && nextNorthOpen ? " " : "_");
        }
        o.println();
    }

    private void printAsciiArtHelperRow(final PrintStream o, final int y) {
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

    /**
     * @see MazePrinter#printAsciiArt(PrintStream). Prints to System.out
     */
    public void printAsciiArt() {
        printAsciiArt(System.out);
    }

    /**
     * Mostly for debugging, creates an SVG file with rectangles representing the walls of the {@link this#linearWallModel}
     * @param name filename to create
     */
    public void printTestSvg(final String name) {
        if (linearWallModel == null) {
            throw new IllegalStateException("linearWallModel can't be null");
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

            final int rectWidth = 4, rectScale = 20;
            for (final LinearWallModel.Wall wall: linearWallModel.walls) {
                final boolean vertical = Objects.equals(wall.start.x, wall.end.x);
                final double addX = vertical ? rectWidth : 0;
                final double addY = vertical ? 0 : rectWidth;
                fileWriter.append("<rect style=\"stroke:#000000;fill:none\" ")
                    .append("x=\"").append(Double.toString(wall.start.x * rectScale - addX)).append("\" ")
                    .append("y=\"").append(Double.toString(wall.start.y * rectScale - addY)).append("\" ")
                    .append("width=\"").append(vertical ? "4" : Double.toString(Math.abs(wall.end.x - wall.start.x) * rectScale - rectWidth)).append("\" ")
                    .append("height=\"").append(vertical ? Double.toString(Math.abs(wall.end.y - wall.start.y) * rectScale - rectWidth) : Integer.toString(rectWidth)).append("\" ")
                    .append("/>");
            }

            fileWriter.append("</g>\n")
                .append("</svg>\n");
            fileWriter.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void printSvg(final String name) throws IOException {
        printSvg(name, null);
    }

    /**
     * Prints an SVG with shapes representing cut-out sections that will be the walls and floor of a maze
     * @param name filename to create
     */
    public void printSvg(final String name, final CalibrationRectangle calibrationRectangle) throws IOException {
        if (sheetWallModel == null) {
            throw new IllegalStateException("sheetWallModel can't be null");
        }
        try (final FileWriter fileWriter = new FileWriter(name)) {
            final InputStream headerStream = getClass().getResourceAsStream("/header.svg");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(headerStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileWriter.append(line).append("\n");
            }
            final SvgElementGenerator svgElementGenerator = new SvgElementGenerator();

            fileWriter.append("<g id=\"floor\">");
            for (final com.adashrod.graphgeneration.mazes.models.Path notch: sheetWallModel.floorNotches.paths) {
                final Path svgPath = svgElementGenerator.modelPathToSvgPath(notch);
                fileWriter.append(svgElementGenerator.pathToSvgText(svgPath, precision));
            }

            for (final com.adashrod.graphgeneration.mazes.models.Path outlinePath: sheetWallModel.floorOutline.paths) {
                final Path svgPath = svgElementGenerator.modelPathToSvgPath(outlinePath);
                svgPath.style = svgPath.style.replace("#000000", "#ff0000");
                fileWriter.append(svgElementGenerator.pathToSvgText(svgPath, precision));
            }
            fileWriter.append("</g>\n");

            fileWriter.append("<g id=\"walls\">");
            for (final Shape shape: sheetWallModel.walls) {
                for (final com.adashrod.graphgeneration.mazes.models.Path wall: shape.paths) {
                    final Path svgPath = svgElementGenerator.modelPathToSvgPath(wall);
                    fileWriter.append(svgElementGenerator.pathToSvgText(svgPath, precision));
                }
            }
            fileWriter.append("</g>\n");

            fileWriter.append("<g id=\"floor-numbers\">");
            for (final VectorNumber floorNumber: sheetWallModel.floorNumbers) {
                fileWriter.append(svgElementGenerator.vectorNumberToSvgText(floorNumber, precision));
            }
            fileWriter.append("</g>\n");

            fileWriter.append("<g id=\"wall-numbers\">");
            for (final VectorNumber wallNumber: sheetWallModel.wallLabels.values()) {
                fileWriter.append(svgElementGenerator.vectorNumberToSvgText(wallNumber, precision));
            }
            fileWriter.append("</g>\n");

            if (calibrationRectangle != null) {
                fileWriter.append(svgElementGenerator.rectToSvgText(buildCalibrationRectangle(calibrationRectangle), precision));
            }

            fileWriter.append("</svg>\n");
            fileWriter.flush();
        }
    }

    private Rect buildCalibrationRectangle(final CalibrationRectangle calibrationRectangle) {
        final BigDecimal width, height, x, y;
        width = calibrationRectangle.getUnit().getPixelsPer().multiply(new BigDecimal(calibrationRectangle.getWidth()));
        height = calibrationRectangle.getUnit().getPixelsPer().multiply(new BigDecimal(calibrationRectangle.getHeight()));
        if (calibrationRectangle.isLeftAligned()) {
            x = ZERO;
        } else {
            x = calibrationRectangle.getMaxWidth().subtract(width);
        }
        if (calibrationRectangle.isTopAligned()) {
            y = ZERO;
        } else {
            y = calibrationRectangle.getMaxHeight().subtract(height);
        }
        final Rect rect = new Rect("calibration-rectangle", width, height, x, y);
        rect.style = rect.style.replace("000000", "00ff00");
        return rect;
    }
}
