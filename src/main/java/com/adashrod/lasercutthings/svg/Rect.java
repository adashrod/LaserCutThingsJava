package com.adashrod.lasercutthings.svg;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * an SVG <rect/> element
 * @author adashrod@gmail.com
 */
public class Rect {
    public String style = "stroke:#000000;fill:none;";//fill-opacity:1;fill-rule:evenodd;stroke:#000000;stroke-width:2;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1";
    public String id;
    public BigDecimal width;
    public BigDecimal height;
    public BigDecimal x;
    public BigDecimal y;

    public Rect(final String id, final BigDecimal size, final BigDecimal x, final BigDecimal y) {
        this.id = id;
        this.width = size;
        this.height = size;
        this.x = x;
        this.y = y;
    }

    public Rect(final String id, final BigDecimal width, final BigDecimal height, final BigDecimal x, final BigDecimal y) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void scale(final BigDecimal factor) {
        width = width.multiply(factor).stripTrailingZeros();
        height = height.multiply(factor).stripTrailingZeros();
        x = x.multiply(factor).stripTrailingZeros();
        y = y.multiply(factor).stripTrailingZeros();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(width);
        result = prime * result + Objects.hashCode(height);
        result = prime * result + Objects.hashCode(x);
        result = prime * result + Objects.hashCode(y);
        return result;
    }

    @Override
    public boolean equals(final Object anObject) {
        if (!(anObject instanceof Rect)) {
            return false;
        }
        final Rect aRect = (Rect) anObject;
        return width.equals(aRect.width) && height.equals(aRect.height) && x.equals(aRect.x) && y.equals(aRect.y);
    }

}
