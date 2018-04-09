package com.adashrod.graphgeneration.cuttingboardgrids;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.svg.Path;
import com.adashrod.graphgeneration.svg.Rect;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static com.adashrod.graphgeneration.cuttingboardgrids.Side.BOTTOM;
import static com.adashrod.graphgeneration.cuttingboardgrids.Side.LEFT;
import static com.adashrod.graphgeneration.cuttingboardgrids.Side.RIGHT;
import static com.adashrod.graphgeneration.cuttingboardgrids.Side.TOP;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

/**
 * Created by aaron on 2018-03-23.
 */
public class CuttingBoard {
    public static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final BigDecimal inchFullPixelSize = BigDecimal.valueOf(254);
    private final BigDecimal inchHalfPixelSize = inchFullPixelSize.divide(TWO);
    private final BigDecimal inchQuarterPixelSize = inchHalfPixelSize.divide(TWO);
    private final BigDecimal inchEighthPixelSize = inchQuarterPixelSize.divide(TWO);
    private final BigDecimal inchSixteenthPixelSize = inchEighthPixelSize.divide(TWO);
    private final BigDecimal inchThirtySecondPixelSize = inchSixteenthPixelSize.divide(TWO);
    private final BigDecimal cmFullPixelSize = BigDecimal.valueOf(100);
    private final BigDecimal cmHalfPixelSize = cmFullPixelSize.divide(TWO);
    private final BigDecimal cmTenthPixelSize = cmFullPixelSize.divide(TEN);
    private final BigDecimal cmTwentiethPixelSize = cmTenthPixelSize.divide(TWO);
    private final BigDecimal cmHundredthPixelSize = cmTenthPixelSize.divide(TEN);

    private final CuttingBoardConfig config;
    private final Deque<Rect> rects = new LinkedList<>();
    private final Deque<Path> paths = new LinkedList<>();
    private final Map<Rect, OrderedPair<Integer>> rectanglePositions = new HashMap<>();

    public CuttingBoard(final CuttingBoardConfig config) {
        this.config = config;
        for (int i = 0; i < config.numInchCols; i++) {
            for (int j = 0; j < config.numInchRows; j++) {
                final BigDecimal xOffset = config.inchStartX.add(inchFullPixelSize.multiply(BigDecimal.valueOf(i)));
                final BigDecimal yOffset = config.inchStartY.add(inchFullPixelSize.multiply(BigDecimal.valueOf(j)));
                final Rect rect = new Rect(String.format("rect-inch-%d-%d", i, j), inchFullPixelSize, xOffset, yOffset);
                rectanglePositions.put(rect, new OrderedPair<>(i, j));
//                rects.add(rect);

                makeAllInchPaths(rect, xOffset, yOffset);
            }
        }

        makeRectSubstitutionPaths(config.numInchCols, config.numInchRows, inchFullPixelSize, config.inchStartX, config.inchStartY, false);
        makeRectSubstitutionPaths(config.numInchRows, config.numInchCols, inchFullPixelSize, config.inchStartX, config.inchStartY, true);

        for (int i = 0; i < config.numCmCols; i++) {
            for (int j = 0; j < config.numCmRows; j++) {
                final BigDecimal xOffset = config.cmStartX.add(cmFullPixelSize.multiply(BigDecimal.valueOf(i)));
                final BigDecimal yOffset = config.cmStartY.add(cmFullPixelSize.multiply(BigDecimal.valueOf(j)));
                final Rect rect = new Rect(String.format("rect-cm-%d-%d", i, j), cmFullPixelSize, xOffset, yOffset);
                rectanglePositions.put(rect, new OrderedPair<>(i, j));
//                rects.add(rect);

                makeAllCmPaths(rect, xOffset, yOffset);
            }
        }

        makeRectSubstitutionPaths(config.numCmCols, config.numCmRows, cmFullPixelSize, config.cmStartX, config.cmStartY, false);
        makeRectSubstitutionPaths(config.numCmRows, config.numCmCols, cmFullPixelSize, config.cmStartX, config.cmStartY, true);

        rects.forEach((final Rect r) -> r.scale(config.scaleFactor));
        paths.forEach((final Path p) -> p.scale(config.scaleFactor));
    }

    public static CuttingBoardConfig configure() {
        return new CuttingBoardConfig();
    }

    private void makeRectSubstitutionPaths(final int numLanes, final int lengthMultiplier, final BigDecimal laneWidth,
            final BigDecimal xStart, final BigDecimal yStart, final boolean transpose) {
        if (xStart == null || yStart == null) {
            return;
        }
        final BigDecimal something = laneWidth.multiply(BigDecimal.valueOf(lengthMultiplier)).add(transpose ? xStart : yStart);
        for (int i = 0; i <= numLanes; i++) {
            final BigDecimal incrementalPos = laneWidth.multiply(BigDecimal.valueOf(i)).add(transpose ? yStart : xStart);
            if (transpose) {
                paths.add(new Path(xStart, incrementalPos, something, incrementalPos));
            } else {
                paths.add(new Path(incrementalPos, yStart, incrementalPos, something));
            }
        }
    }

    private void makeAllInchPaths(final Rect rect, final BigDecimal xOffset, final BigDecimal yOffset) {
        paths.addAll(makeInchPaths(rect, xOffset, yOffset, TOP));
        paths.addAll(makeInchPaths(rect, xOffset, yOffset, RIGHT));
        paths.addAll(makeInchPaths(rect, xOffset, yOffset, BOTTOM));
        paths.addAll(makeInchPaths(rect, xOffset, yOffset, LEFT));
    }

    private Collection<Path> makeInchPaths(final Rect rect, final BigDecimal xOffset, final BigDecimal yOffset,
            final Side side) {
        final Collection<Path> newPaths = new ArrayList<>();
        newPaths.add(makePath(rect, "half", xOffset, yOffset, inchHalfPixelSize, inchEighthPixelSize, 1, side));
        for (final int i: new int[]{1, 3}) {
            newPaths.add(makePath(rect, "quarter", xOffset, yOffset, inchQuarterPixelSize, inchSixteenthPixelSize, i,
                side));
        }
        for (final int i: new int[]{1, 3, 5, 7}) {
            newPaths.add(makePath(rect, "eighth", xOffset, yOffset, inchEighthPixelSize, inchThirtySecondPixelSize, i,
                side));
        }
        return newPaths;
    }

    private void makeAllCmPaths(final Rect rect, final BigDecimal xOffset, final BigDecimal yOffset) {
        paths.addAll(makeCmPaths(rect, xOffset, yOffset, TOP));
        paths.addAll(makeCmPaths(rect, xOffset, yOffset, RIGHT));
        paths.addAll(makeCmPaths(rect, xOffset, yOffset, BOTTOM));
        paths.addAll(makeCmPaths(rect, xOffset, yOffset, LEFT));
    }

    private Collection<Path> makeCmPaths(final Rect rect, final BigDecimal xOffset, final BigDecimal yOffset,
            final Side side) {
        final Collection<Path> newPaths = new ArrayList<>();
        final OrderedPair<Integer> rectPos = rectanglePositions.get(rect);
        if (config.printAllCmHashes ||
               (side == TOP &&    (rectPos.y == 0 ||                    rectPos.y == config.numCmRows / 2) ||
                side == RIGHT &&  (rectPos.x == config.numCmCols - 1 || rectPos.x == config.numCmCols / 2 - 1) ||
                side == BOTTOM && (rectPos.y == config.numCmRows - 1 || rectPos.y == config.numCmRows / 2 - 1) ||
                side == LEFT &&   (rectPos.x == 0 ||                    rectPos.x == config.numCmCols / 2))) {
            newPaths.add(makePath(rect, "half", xOffset, yOffset, cmHalfPixelSize, cmTenthPixelSize, 1, side));
            for (final int i: new int[]{1, 2, 3, 4, 6, 7, 8, 9}) { // all mm marks except half-cm
                newPaths.add(makePath(rect, "mm", xOffset, yOffset, cmTenthPixelSize, cmTwentiethPixelSize, i,
                    side));
            }
        }
        return newPaths;
    }

    private Path makePath(final Rect rect, final String hashMarkName, final BigDecimal xOffset, final BigDecimal yOffset,
            final BigDecimal baseOffsetOfHashMark, final BigDecimal hashMarkLength, final long ordinal,
            final Side side) {
        final Path path = new Path();
        path.id = String.format("%s-path-%s-%s-%s", rect.id, hashMarkName, ordinal, side.name().toLowerCase());
        BigDecimal xStart = xOffset.add(baseOffsetOfHashMark.multiply(BigDecimal.valueOf(ordinal)));
        BigDecimal xEnd = xStart;
        BigDecimal yStart = yOffset;
        BigDecimal yEnd = yOffset.add(hashMarkLength);
        if (side.flip) {
            yEnd = yStart.add(rect.height).subtract(yEnd.subtract(yStart));
            yStart = yStart.add(rect.height);
        }
        if (side.transpose) {
            // normalize
            xStart = xStart.subtract(xOffset);
            xEnd = xEnd.subtract(xOffset);
            yStart = yStart.subtract(yOffset);
            yEnd = yEnd.subtract(yOffset);
            // transpose
            BigDecimal temp = xStart;
            xStart = yStart;
            yStart = temp;
            temp = xEnd;
            xEnd = yEnd;
            yEnd = temp;
            // denormalize
            xStart = xStart.add(xOffset);
            xEnd = xEnd.add(xOffset);
            yStart = yStart.add(yOffset);
            yEnd = yEnd.add(yOffset);
        }
        path.start = new OrderedPair<>(xStart, yStart);
        path.end = new OrderedPair<>(xEnd, yEnd);
        return path;
    }

    private void convertRectsToPaths() {
        while (!rects.isEmpty()) {
            final Rect rect = rects.removeFirst();
            final BigDecimal xLeft = rect.x, xRight = rect.x.add(rect.width), yTop = rect.y, yBottom = rect.y.add(rect.height);
            final Path top = new Path(xLeft, yTop, xRight, yTop);
            final Path right = new Path(xRight, yTop, xRight, yBottom);
            final Path bottom = new Path(xLeft, yBottom, xRight, yBottom);
            final Path left = new Path(xLeft, yTop, xLeft, yBottom);
            paths.addAll(Arrays.asList(top, right, bottom, left));
        }
    }

    private boolean ifCollinearMerge(final Path firstPath, final Path secondPath) {
        final BigDecimal x = firstPath.start.x;
        final BigDecimal[] xes = {firstPath.end.x, secondPath.start.x, secondPath.end.x};
        int i;
        for (i = 0; i < xes.length; i++) {
            if (!x.equals(xes[i])) {
                break;
            }
        }
        if (i == xes.length) {
            final BigDecimal mergedYStart = firstPath.start.y.min(firstPath.end.y).min(secondPath.start.y).min(secondPath.end.y);
            final BigDecimal mergedYEnd = firstPath.start.y.max(firstPath.end.y).max(secondPath.start.y).max(secondPath.end.y);
            firstPath.start.y = mergedYStart;
            firstPath.end.y = mergedYEnd;
            return true; // all xes ==, ifCollinearMerge on y-axis
        }
        final BigDecimal y = firstPath.start.y;
        final BigDecimal[] ys = {firstPath.end.y, secondPath.start.y, secondPath.end.y};
        for (i = 0; i < ys.length; i++) {
            if (!y.equals(ys[i])) {
                break;
            }
        }
        if (i == ys.length) {
            final BigDecimal mergedXStart = firstPath.start.x.min(firstPath.end.x).min(secondPath.start.x).min(secondPath.end.x);
            final BigDecimal mergedXEnd = firstPath.start.x.max(firstPath.end.x).max(secondPath.start.x).max(secondPath.end.x);
            firstPath.start.x = mergedXStart;
            firstPath.end.x = mergedXEnd;
            return true; // all ys ==, ifCollinearMerge on x-axis
        }
        return false;
    }

    private boolean ifAdjacentAndCollinearMerge(final Path firstPath, final Path secondPath) {
        if (firstPath.start.x.equals(secondPath.start.x)) {
            if (firstPath.start.y.equals(secondPath.start.y)) {
                return ifCollinearMerge(firstPath, secondPath);
            }
        }
        if (firstPath.start.x.equals(secondPath.end.x)) {
            if (firstPath.start.y.equals(secondPath.end.y)) {
                return ifCollinearMerge(firstPath, secondPath);
            }
        }
        if (firstPath.end.x.equals(secondPath.start.x)) {
            if (firstPath.end.y.equals(secondPath.start.y)) {
                return ifCollinearMerge(firstPath, secondPath);
            }
        }
        if (firstPath.end.x.equals(secondPath.end.x)) {
            if (firstPath.end.y.equals(secondPath.end.y)) {
                return ifCollinearMerge(firstPath, secondPath);
            }
        }
        return false;
    }

    private void optimize() {
        System.out.printf("start optimization: %d rects, %d paths\n", rects.size(), paths.size());
        convertRectsToPaths();
        System.out.printf("converted rects to paths: %d rects, %d paths\n", rects.size(), paths.size());
        // O(n^2) because it's good enough
        for (int i = 0; i < paths.size(); i++) {
            final Path firstPath = ((LinkedList<Path>) paths).get(i);
            for (final Iterator<Path> iterator = paths.iterator(); iterator.hasNext(); ) {
                final Path secondPath = iterator.next();
                if (firstPath == secondPath) { // don't compare to self
                    continue;
                }
                if (firstPath.equals(secondPath)) { // but do remove dupes
                    iterator.remove();
                    continue;
                }
                if (ifAdjacentAndCollinearMerge(firstPath, secondPath)) {
                    iterator.remove();
                    i--;
                    break;
                }
            }
        }
        System.out.printf("post-merge: %d paths\n", paths.size());
    }

    public void print(final String filename) throws IOException {
        optimize();
        final InputStream headerStream = getClass().getResourceAsStream("/header.svg");
        final FileWriter fileWriter = new FileWriter(filename);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(headerStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            fileWriter.append(line).append("\n");
        }
        fileWriter.append("<g ")
            .append("inkscape:label=\"Layer 1\" ")
            .append("inkscape:groupmode=\"layer\" ")
            .append("id=\"layer1\">");

        for (final Rect rect: rects) {
            fileWriter
                .append("<rect style=\"").append(rect.style).append("\" ")
//                .append("id=\"").append(rect.id).append("\" ")
                .append("width=\"").append(rect.width.toString()).append("\" ")
                .append("height=\"").append(rect.height.toString()).append("\" ")
                .append("x=\"").append(rect.x.toString()).append("\" ")
                .append("y=\"").append(rect.y.toString()).append("\"/>");
        }
        for (final Path path: paths) {
            fileWriter.append("<path style=\"").append(path.style).append("\" ")
//                .append("id=\"").append(path.id).append("\" ")
                .append("d=\"").append(path.getD()).append("\"/>");
        }

        fileWriter.append("</g>\n")
            .append("</svg>\n");
        fileWriter.flush();
        fileWriter.close();
    }

    public static class CuttingBoardConfig {
        int numInchRows;
        int numInchCols;
        BigDecimal inchStartX;
        BigDecimal inchStartY;
        int numCmRows;
        int numCmCols;
        BigDecimal cmStartX;
        BigDecimal cmStartY;
        BigDecimal scaleFactor = ONE;
        boolean printAllCmHashes;

        public CuttingBoardConfig withNumInchRows(final int numInchRows) {
            this.numInchRows = numInchRows;
            return this;
        }

        public CuttingBoardConfig withNumInchCols(final int numInchCols) {
            this.numInchCols = numInchCols;
            return this;
        }

        public CuttingBoardConfig withInchStartX(final String inchStartX) {
            this.inchStartX = new BigDecimal(inchStartX);
            return this;
        }

        public CuttingBoardConfig withInchStartY(final String inchStartY) {
            this.inchStartY = new BigDecimal(inchStartY);
            return this;
        }

        public CuttingBoardConfig withNumCmRows(final int numCmRows) {
            this.numCmRows = numCmRows;
            return this;
        }

        public CuttingBoardConfig withNumCmCols(final int numCmCols) {
            this.numCmCols = numCmCols;
            return this;
        }

        public CuttingBoardConfig withCmStartX(final String cmStartX) {
            this.cmStartX = new BigDecimal(cmStartX);
            return this;
        }

        public CuttingBoardConfig withCmStartY(final String cmStartY) {
            this.cmStartY = new BigDecimal(cmStartY);
            return this;
        }

        public CuttingBoardConfig withScaleFactor(final BigDecimal scaleFactor) {
            this.scaleFactor = scaleFactor;
            return this;
        }

        public CuttingBoardConfig withPrintAllCmHashes(final boolean printAllCmHashes) {
            this.printAllCmHashes = printAllCmHashes;
            return this;
        }
    }
}
