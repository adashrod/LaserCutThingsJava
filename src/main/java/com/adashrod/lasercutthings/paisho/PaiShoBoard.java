package com.adashrod.lasercutthings.paisho;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.common.Unit;
import com.adashrod.lasercutthings.svg.Circle;
import com.adashrod.lasercutthings.svg.Path;
import com.adashrod.lasercutthings.svg.SvgElementGenerator;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static java.math.BigDecimal.ZERO;

/**
 * Created by adashrod@gmail.com on 2018-04-13.
 */
public class PaiShoBoard {
    private final Circle circle;
    private final Path topToRightDiagonal;
    private final Path rightToBottomDiagonal;
    private final Path bottomToLeftDiagonal;
    private final Path leftToTopDiagonal;
    private final Collection<Path> lines = new ArrayList<>();
    private final int precision = 5;

    private final BigDecimal gridSize = Unit.INCHES.getPixelsPer();

    public PaiShoBoard(final int numLanes, final int diagonalIntersectionDisplacement) {
        circle = new Circle();
        circle.center = new OrderedPair<>(ZERO, ZERO);
        circle.radius = gridSize.multiply(new BigDecimal(numLanes / 2));

        for (int i = 1; i < numLanes; i++) {
            final int p = i - numLanes / 2;
            final BigDecimal y = gridSize.multiply(new BigDecimal(p));
            // circle: x^2 + y^2 = circle.radius^2
            // line:   y = p
            //         x = \/(circle.radius^2 - p^2)
            final BigDecimal x = new BigDecimal(Math.sqrt(circle.radius.multiply(circle.radius).subtract(y.multiply(y)).doubleValue()));
            lines.add(new Path(new OrderedPair<>(x.negate(), y), new OrderedPair<>(x, y)));
            lines.add(new Path(new OrderedPair<>(y, x.negate()), new OrderedPair<>(y, x)));
        }
        // circle: x^2 + y^2 = circle.radius^2
        // line:   y = 1x + (circle.radius - 2*gridSize) (leftToTop line)
        final BigDecimal yIntercept = circle.radius.subtract(gridSize.multiply(new BigDecimal(diagonalIntersectionDisplacement)));
        final Pair<Double, Double> xPair = solveQuadratic(2,
            yIntercept.multiply(new BigDecimal(-1-1)).doubleValue(),
            yIntercept.multiply(yIntercept).subtract(circle.radius.multiply(circle.radius)).doubleValue());
        final Pair<Double, Double> yPair = solveQuadratic(2,
            yIntercept.multiply(new BigDecimal(2)).doubleValue(),
            yIntercept.multiply(yIntercept).subtract(circle.radius.multiply(circle.radius)).doubleValue());
        final OrderedPair<BigDecimal> diagStart = new OrderedPair<>(new BigDecimal(xPair.getKey()), new BigDecimal(yPair.getKey())),
            diagEnd = new OrderedPair<>(new BigDecimal(xPair.getValue()), new BigDecimal(yPair.getValue()));
        topToRightDiagonal = new Path(diagStart, diagEnd);
        topToRightDiagonal.id = "top-to-right";
        rightToBottomDiagonal = new Path(new OrderedPair<>(diagStart.x, diagStart.y.negate()), new OrderedPair<>(diagEnd.x, diagEnd.y.negate()));
        rightToBottomDiagonal.id = "right-to-bottom";
        bottomToLeftDiagonal = new Path(new OrderedPair<>(diagStart.x.negate(), diagStart.y.negate()), new OrderedPair<>(diagEnd.x.negate(), diagEnd.y.negate()));
        bottomToLeftDiagonal.id = "bottom-to-left";
        leftToTopDiagonal = new Path(new OrderedPair<>(diagStart.x.negate(), diagStart.y), new OrderedPair<>(diagEnd.x.negate(), diagEnd.y));
        leftToTopDiagonal.id = "left-to-top";
    }

    private Pair<Double, Double> solveQuadratic(final double a, final double b, final double c) {
        final double left = - b / (2 * a);
        final double right = Math.sqrt(b * b - 4 * a * c) / (2 * a);
        return new Pair<>(left - right, left + right);
    }

    public void printSvg(final String name) throws IOException {
        try (final FileWriter fileWriter = new FileWriter(name)) {
            final InputStream headerStream = getClass().getResourceAsStream("/header.svg");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(headerStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileWriter.append(line).append("\n");
            }
            final SvgElementGenerator svgElementGenerator = new SvgElementGenerator();

            fileWriter.append("<g id=\"board\">");

            fileWriter.append(svgElementGenerator.circleToSvgText(circle, precision));
            for (final Path path: lines) {
                fileWriter.append(svgElementGenerator.pathToSvgText(path, precision));
            }

            fileWriter.append(svgElementGenerator.pathToSvgText(topToRightDiagonal, precision));
            fileWriter.append(svgElementGenerator.pathToSvgText(rightToBottomDiagonal, precision));
            fileWriter.append(svgElementGenerator.pathToSvgText(bottomToLeftDiagonal, precision));
            fileWriter.append(svgElementGenerator.pathToSvgText(leftToTopDiagonal, precision));

            fileWriter.append("</g>\n");
            fileWriter.append("</svg>\n");
            fileWriter.flush();
        }
    }
}
