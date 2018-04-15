package com.adashrod.graphgeneration.svg;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.models.Path;
import com.adashrod.graphgeneration.mazes.models.Shape;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adashrod@gmail.com
 */
public class VectorNumber {
    public int number;
    public BigDecimal width, height;
    public OrderedPair<BigDecimal> position;
    public static final BigDecimal SPACING = new BigDecimal("2");

    private static final Map<Character, Shape> charMap = new HashMap<>();
    static {
        final Shape zero = new Shape(),
            one = new Shape(),
            two = new Shape(),
            three = new Shape(),
            four = new Shape(),
            five = new Shape(),
            six = new Shape(),
            seven = new Shape(),
            eight = new Shape(),
            nine = new Shape();
        charMap.put('0', zero);
        charMap.put('1', one);
        charMap.put('2', two);
        charMap.put('3', three);
        charMap.put('4', four);
        charMap.put('5', five);
        charMap.put('6', six);
        charMap.put('7', seven);
        charMap.put('8', eight);
        charMap.put('9', nine);
        final Path zeroPath = new Path();
        zeroPath.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(6)))
            .setClosed(true);
        zero.addPath(zeroPath);
        final Path onePathMain = new Path(), onePathBase = new Path();
        onePathMain.addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(8)))
            .setClosed(false);
        onePathBase.addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .setClosed(false);
        one.addPath(onePathMain).addPath(onePathBase);
        final Path twoPath = new Path();
        twoPath.addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(8)))
            .setClosed(false);
        two.addPath(twoPath);
        final Path threePathTop = new Path(), threePathBottom = new Path();
        threePathTop.addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .setClosed(false);
        threePathBottom.addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(7)))
            .setClosed(false);
        three.addPath(threePathTop).addPath(threePathBottom);
        final Path fourPathBent = new Path(), fourPathStem = new Path(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)),
            new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)));
        fourPathBent.addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(4)))
            .setClosed(false);
        four.addPath(fourPathBent).addPath(fourPathStem);
        final Path fivePath = new Path();
        fivePath.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(7)))
            .setClosed(false);
        five.addPath(fivePath);
        final Path sixPath = new Path();
        sixPath.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(5)))
            .setClosed(false);
        six.addPath(sixPath);
        final Path sevenPath = new Path();
        sevenPath.addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .setClosed(false);
        seven.addPath(sevenPath);
        final Path eightPathTop = new Path(), eightPathBottom = new Path();
        eightPathTop.addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .setClosed(true);
        eightPathBottom.addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .setClosed(false);
        eight.addPath(eightPathTop).addPath(eightPathBottom);
        final Path ninePath = new Path();
        ninePath.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(0)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(0), new BigDecimal(7)))
            .setClosed(false);
        nine.addPath(ninePath);
    }

    public VectorNumber(final int number, final BigDecimal width, final BigDecimal height,
            final OrderedPair<BigDecimal> position) {
        this.number = number;
        this.width = width;
        this.height = height;
        this.position = position;
    }

    public static Shape characterToShape(final char c) {
        return Shape.copy(charMap.get(c));
    }
}
