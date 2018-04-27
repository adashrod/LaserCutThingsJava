package com.adashrod.graphgeneration;

import com.adashrod.graphgeneration.cuttingboardgrids.CuttingBoard;
import com.adashrod.graphgeneration.mazes.MazePrinter;
import com.adashrod.graphgeneration.mazes.algorithms.PrimsAlgorithm;
import com.adashrod.graphgeneration.mazes.factories.LinearWallModelGenerator;
import com.adashrod.graphgeneration.mazes.factories.RectangularWallModelGenerator;
import com.adashrod.graphgeneration.mazes.factories.SheetWallModelGenerator;
import com.adashrod.graphgeneration.mazes.models.CalibrationRectangle;
import com.adashrod.graphgeneration.mazes.models.LinearWallModel;
import com.adashrod.graphgeneration.mazes.models.Maze;
import com.adashrod.graphgeneration.mazes.models.RectangularWallModel;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;

import java.io.IOException;
import java.math.BigDecimal;

import static com.adashrod.graphgeneration.common.Unit.INCHES;
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

    public static void main(final String... args) throws Exception {
        final Maze maze = new Maze(12, 12);
        final LinearWallModel linearWallModel;
        final RectangularWallModel rectangularWallModel;
        SheetWallModel sheetWallModel;

        final long seed = System.currentTimeMillis();
        maze.build(new PrimsAlgorithm().setSeed(seed));
        maze.getGrid()[0][0].openWall(WEST);
        System.out.printf("seed: %d\n", seed);
        new MazePrinter(maze).printAsciiArt();
        linearWallModel = new LinearWallModelGenerator(maze).generate();
        new MazePrinter(linearWallModel).printTestSvg("actualTestMaze.svg");
        rectangularWallModel = new RectangularWallModelGenerator(linearWallModel).generate();
        sheetWallModel = new SheetWallModelGenerator(rectangularWallModel, SheetWallModelGenerator.configure()
            .withUnit(INCHES)
            .withMaterialThickness(new BigDecimal(".118"))
            .withHallWidth(new BigDecimal(".5"))
            .withSeparationSpace(new BigDecimal(".05"))
            .withWallHeight(new BigDecimal(".15"))
            .withNotchHeight(new BigDecimal(".2"))
            .withMaxWidth(new BigDecimal("19.5"))
            .withMaxHeight(new BigDecimal("11"))
            .build()).generate();
        // todo: add a unit param to MP ctor?
        new MazePrinter(sheetWallModel, new BigDecimal("19.5").multiply(INCHES.getPixelsPer()), new BigDecimal("11").multiply(INCHES.getPixelsPer())).printSvg("actualTestMazeCuts.svg",
            CalibrationRectangle.configure()
            .withWidth(6).withHeight(6).withUnit(INCHES).withBottomAlignment().withRightAlignment()
            .build());
    }
}
