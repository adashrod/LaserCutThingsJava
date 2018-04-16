package com.adashrod.graphgeneration.svg;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.math.BigDecimal;

/**
 * an SVG <circle/> element
 * @author adashrod@gmail.com
 */
public class Circle {
    public OrderedPair<BigDecimal> center;
    public BigDecimal radius;

    public Circle() {}

    public Circle(final OrderedPair<BigDecimal> center, final BigDecimal radius) {
        this.center = center;
        this.radius = radius;
    }
}
