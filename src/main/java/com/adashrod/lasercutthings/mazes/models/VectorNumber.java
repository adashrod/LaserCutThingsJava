package com.adashrod.lasercutthings.mazes.models;

import com.adashrod.lasercutthings.common.OrderedPair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * A VectorNumber is a model with a numeric value, and context of width, height, and position
 * @author adashrod@gmail.com
 */
public class VectorNumber {
    public final int number;
    public final BigDecimal width, height;
    public final OrderedPair<BigDecimal> position;
    public static final int CHARACTER_WIDTH = 6;
    public static final int CHARACTER_HEIGHT = 10;

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
        zeroPath.addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(3)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(7)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(7)))
            .setClosed(true);
        zero.addPath(zeroPath);
        final Path onePathMain = new Path(), onePathBase = new Path();
        onePathMain.addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(3), new BigDecimal(9)))
            .setClosed(false);
        onePathBase.addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .setClosed(false);
        one.addPath(onePathMain).addPath(onePathBase);
        final Path twoPath = new Path();
        twoPath.addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(9)))
            .setClosed(false);
        two.addPath(twoPath);
        final Path threePathTop = new Path(), threePathBottom = new Path();
        threePathTop.addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(5)))
            .setClosed(false);
        threePathBottom.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .setClosed(false);
        three.addPath(threePathTop).addPath(threePathBottom);
        final Path fourPathBent = new Path(), fourPathStem = new Path(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)),
            new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)));
        fourPathBent.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(5)))
            .setClosed(false);
        four.addPath(fourPathBent).addPath(fourPathStem);
        final Path fivePath = new Path();
        fivePath.addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .setClosed(false);
        five.addPath(fivePath);
        final Path sixPath = new Path();
        sixPath.addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(6)))
            .setClosed(false);
        six.addPath(sixPath);
        final Path sevenPath = new Path();
        sevenPath.addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .setClosed(false);
        seven.addPath(sevenPath);
        final Path eightPathTop = new Path(), eightPathBottom = new Path();
        eightPathTop.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(5)))
            .setClosed(true);
        eightPathBottom.addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(6)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(5)))
            .setClosed(false);
        eight.addPath(eightPathTop).addPath(eightPathBottom);
        final Path ninePath = new Path();
        ninePath.addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(5)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(4)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(1)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(2)))
            .addPoint(new OrderedPair<>(new BigDecimal(5), new BigDecimal(8)))
            .addPoint(new OrderedPair<>(new BigDecimal(4), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(2), new BigDecimal(9)))
            .addPoint(new OrderedPair<>(new BigDecimal(1), new BigDecimal(8)))
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

    public VectorNumber translate(final OrderedPair<BigDecimal> delta) {
        position.x = position.x.add(delta.x);
        position.y = position.y.add(delta.y);
        return this;
    }

    /**
     * Given a digit character, returns a {@link Shape} with points describing the shape of that numeral
     * @param c a digit char
     * @return a shape that looks like the numeral
     */
    public static Shape characterToShape(final char c) {
        return Shape.copy(charMap.get(c));
    }
}
