package com.adashrod.graphgeneration;

import com.adashrod.graphgeneration.cuttingboardgrids.CuttingBoard;
import com.adashrod.graphgeneration.mazes.KruskalsAlgorithm;
import com.adashrod.graphgeneration.mazes.Maze;
import com.adashrod.graphgeneration.mazes.MazeModelGenerator;
import com.adashrod.graphgeneration.mazes.MazePrinter;
import com.adashrod.graphgeneration.mazes.MazeWallModel;
import com.adashrod.graphgeneration.mazes.PrimsAlgorithm;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by aaron on 2018-04-01.
 */
public class Main {
    private static void printSomeBoards() throws IOException {
        final long start = System.currentTimeMillis();
        final BigDecimal scale = new BigDecimal(".354408");
        new CuttingBoard(CuttingBoard.config().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(1).withNumInchCols(19)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(true))
            .print("cuttingBoardTestBoard.svg");
        new CuttingBoard(CuttingBoard.config().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(8).withNumInchCols(6)
            .withCmStartX("1700").withCmStartY("0")
            .withNumCmRows(20).withNumCmCols(16)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(false))
            .print("cuttingBoard-6x8-16x20.svg");
        new CuttingBoard(CuttingBoard.config().withInchStartX("0").withInchStartY("0")
            .withNumInchRows(10).withNumInchCols(8)
            .withCmStartX("2200").withCmStartY("0")
            .withNumCmRows(26).withNumCmCols(20)
            .withScaleFactor(scale)
            .withPrintAllCmHashes(false))
            .print("cuttingBoard-8x10-20x26.svg");
        System.out.printf("took %d ms to gen svg\n", System.currentTimeMillis() - start);
    }

    public static void main(final String... args) throws IOException {
        final Maze maze = new Maze(16, 9);
        MazeWallModel mazeWallModel;

        maze.build(new PrimsAlgorithm());
        System.out.println("prim's:");
        new MazePrinter(maze).printAsciiArt();
        mazeWallModel = new MazeModelGenerator(maze).generateFrom();
        new MazePrinter(mazeWallModel).printTestSvg("primsNs.svg");
        mazeWallModel = new MazeModelGenerator(maze).setFavorEw(true).generateFrom();
        new MazePrinter(mazeWallModel).printTestSvg("primsEw.svg");

        maze.build(new KruskalsAlgorithm());
        System.out.println("kruskal's");
        new MazePrinter(maze).printAsciiArt();
        mazeWallModel = new MazeModelGenerator(maze).setFavorEw(false).generateFrom();
        new MazePrinter(mazeWallModel).printTestSvg("kruskalsNs.svg");
        mazeWallModel = new MazeModelGenerator(maze).setFavorEw(true).generateFrom();
        new MazePrinter(mazeWallModel).printTestSvg("kruskalsEw.svg");
    }
}
