package com.adashrod.graphgeneration.svg;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by aaron on 2018-04-01.
 */
public class Path {
    public final String style = "stroke:#000000";//"fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1";
    public BigDecimal xStart;
    public BigDecimal xEnd;
    public BigDecimal yStart;
    public BigDecimal yEnd;
    public String id;

    public Path() {}

    public Path(final BigDecimal xStart, final BigDecimal yStart, final BigDecimal xEnd, final BigDecimal yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    public String getD() {
        return String.format("M %s,%s %s,%s", xStart, yStart, xEnd, yEnd);
    }

    public void scale(final BigDecimal factor) {
        xStart = xStart.multiply(factor).stripTrailingZeros();
        xEnd = xEnd.multiply(factor).stripTrailingZeros();
        yStart = yStart.multiply(factor).stripTrailingZeros();
        yEnd = yEnd.multiply(factor).stripTrailingZeros();
    }

    @Override
    public String toString() {
        return String.format("Path[(%s,%s) -> (%s,%s)]", xStart, yStart, xEnd, yEnd);
    }

    @Override
    public boolean equals(final Object anObject) {
        if (!(anObject instanceof Path)) {
            return false;
        }
        final Path aPath = (Path) anObject;

        return Objects.equals(xStart, aPath.xStart) &&
            Objects.equals(xEnd, aPath.xEnd) &&
            Objects.equals(yStart, aPath.yStart) &&
            Objects.equals(yEnd, aPath.yEnd);
    }

}
