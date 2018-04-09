package com.adashrod.graphgeneration.svg;

import com.adashrod.graphgeneration.common.OrderedPair;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An SVG <path/> element
 * @author adashrod@gmail.com
 */
public class Path {
    public final String style = "stroke:#000000";//"fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1";
    public OrderedPair<BigDecimal> start;
    public OrderedPair<BigDecimal> end;
    public String id;

    public Path() {}

    public Path(final BigDecimal xStart, final BigDecimal yStart, final BigDecimal xEnd, final BigDecimal yEnd) {
        this.start.x = xStart;
        this.start.y = yStart;
        this.end.x = xEnd;
        this.end.y = yEnd;
    }

    public Path(final OrderedPair<BigDecimal> start, final OrderedPair<BigDecimal> end) {
        this.start = start;
        this.end = end;
    }

    public String getD() {
        return String.format("M %s,%s %s,%s", start.x, start.y, end.x, end.y);
    }

    public void scale(final BigDecimal factor) {
        start.x = start.x.multiply(factor).stripTrailingZeros();
        end.x = end.x.multiply(factor).stripTrailingZeros();
        start.y = start.y.multiply(factor).stripTrailingZeros();
        end.y = end.y.multiply(factor).stripTrailingZeros();
    }

    @Override
    public String toString() {
        return String.format("Path[(%s,%s) -> (%s,%s)]", start.x, start.y, end.x, end.y);
    }

    @Override
    public boolean equals(final Object anObject) {
        if (!(anObject instanceof Path)) {
            return false;
        }
        final Path aPath = (Path) anObject;

        return Objects.equals(start.x, aPath.start.x) &&
            Objects.equals(end.x, aPath.end.x) &&
            Objects.equals(start.y, aPath.start.y) &&
            Objects.equals(end.y, aPath.end.y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(start.x);
        result = prime * result + Objects.hashCode(end.x);
        result = prime * result + Objects.hashCode(start.y);
        result = prime * result + Objects.hashCode(end.y);

        return result;
    }

}
