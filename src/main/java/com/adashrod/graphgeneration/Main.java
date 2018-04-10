package com.adashrod.graphgeneration;

import com.adashrod.graphgeneration.cuttingboardgrids.CuttingBoard;
import com.adashrod.graphgeneration.mazes.MazePrinter;
import com.adashrod.graphgeneration.mazes.algorithms.KruskalsAlgorithm;
import com.adashrod.graphgeneration.mazes.algorithms.PrimsAlgorithm;
import com.adashrod.graphgeneration.mazes.factories.LinearWallModelGenerator;
import com.adashrod.graphgeneration.mazes.factories.SheetWallModelGenerator;
import com.adashrod.graphgeneration.mazes.factories.TopDownRectangularWallModelGenerator;
import com.adashrod.graphgeneration.mazes.models.LinearWallModel;
import com.adashrod.graphgeneration.mazes.models.Maze;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;
import com.adashrod.graphgeneration.mazes.models.TopDownRectangularWallModel;

import java.io.IOException;
import java.math.BigDecimal;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;

/**
 * Created by aaron on 2018-04-01.
 */
public final class Main {
    private Main() {}

    private static void printSomeBoards() throws IOException {
        final long start = System.currentTimeMillis();
        final BigDecimal scale = new BigDecimal(".354408");
        final int calibrationTestBoardCols = 19,
            smallBoardInchWidth = 6, smallBoardInchHeight = 8,
            smallBoardCmWidth = 16, smallBoardCmHeight = 20,
            largeBoardInchWidth = 8, largeBoardInchHeight = 10,
            largeBoardCmWidth = 20, largeBoardCmHeight = 26;
        new CuttingBoard(CuttingBoard.configure().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(1).withNumInchCols(calibrationTestBoardCols)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(true))
            .print("cuttingBoardTestBoard.svg");
        new CuttingBoard(CuttingBoard.configure().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(smallBoardInchHeight).withNumInchCols(smallBoardInchWidth)
            .withCmStartX("1700").withCmStartY("0")
            .withNumCmRows(smallBoardCmHeight).withNumCmCols(smallBoardCmWidth)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(false))
            .print("cuttingBoard-6x8-16x20.svg");
        new CuttingBoard(CuttingBoard.configure().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(largeBoardInchHeight).withNumInchCols(largeBoardInchWidth)
            .withCmStartX("2200").withCmStartY("0")
            .withNumCmRows(largeBoardCmHeight).withNumCmCols(largeBoardCmWidth)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(false))
            .print("cuttingBoard-8x10-20x26.svg");
        System.out.printf("took %d ms to gen svg\n", System.currentTimeMillis() - start);
    }

    public static void main(final String... args) throws IOException {
        final Maze maze = new Maze(8, 8);
        LinearWallModel linearWallModel;
        TopDownRectangularWallModel topDownRectangularWallModel;
        SheetWallModel sheetWallModel;

        maze.build(new PrimsAlgorithm());
        System.out.println("prim's:");
        new MazePrinter(maze).printAsciiArt();
        linearWallModel = new LinearWallModelGenerator(maze).generate();
        new MazePrinter(linearWallModel).printTestSvg("primsNs.svg");
        linearWallModel = new LinearWallModelGenerator(maze).setFavorEw(true).generate();
        new MazePrinter(linearWallModel).printTestSvg("primsEw.svg");

        maze.build(new KruskalsAlgorithm());
        System.out.println("kruskal's");
        new MazePrinter(maze).printAsciiArt();
        linearWallModel = new LinearWallModelGenerator(maze).setFavorEw(false).generate();
        new MazePrinter(linearWallModel).printTestSvg("kruskalsNs.svg");
//        topDownRectangularWallModel = new TopDownRectangularWallModelGenerator(linearWallModel).generate();
//        System.out.printf("kruskalsNs chars:\n%s\n", topDownRectangularWallModel.toString());
        linearWallModel = new LinearWallModelGenerator(maze).setFavorEw(true).generate();
        new MazePrinter(linearWallModel).printTestSvg("kruskalsEw.svg");
//        topDownRectangularWallModel = new TopDownRectangularWallModelGenerator(linearWallModel).generate();
//        System.out.printf("kruskalsEw chars:\n%s\n", topDownRectangularWallModel);

        // testing testing
        final int smallMazeSize = 3;
        final Maze smallMaze = new Maze(smallMazeSize, smallMazeSize);
        smallMaze.build(new PrimsAlgorithm().setSeed(0));
        smallMaze.getGrid()[0][0].openWall(WEST);
        smallMaze.getGrid()[smallMazeSize - 1][smallMazeSize - 1].openWall(EAST);
//        smallMaze.getGrid()[smallMazeSize - 1][smallMazeSize - 1].openWall(SOUTH);

        linearWallModel = new LinearWallModelGenerator(smallMaze).setFavorEw(false).generate();
        topDownRectangularWallModel = new TopDownRectangularWallModelGenerator(linearWallModel).generate();
        System.out.printf("TOP DOWN RECTANGULAR MODEL:\n%s\n", topDownRectangularWallModel);
        sheetWallModel = new SheetWallModelGenerator(topDownRectangularWallModel, SheetWallModelGenerator.configure()
            .withHallWidth(new BigDecimal(10))
            .withMaterialThickness(new BigDecimal(1))
            .withWallHeight(new BigDecimal(5))
            .build()).generate();

        new MazePrinter(smallMaze).printAsciiArt();
        new MazePrinter(linearWallModel).printTestSvg("smallMaze.svg");
    }
}
