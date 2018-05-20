package com.adashrod.lasercutthings.common;

import java.math.BigDecimal;

/**
 * Created by adashrod@gmail.com on 2018-04-15.
 */
public enum Unit {
    INCHES(new BigDecimal("90.000001")),
    CENTIMETERS(new BigDecimal("35.433071")),
    MILLIMETERS(new BigDecimal("3.5433071"));

    private final BigDecimal pixelsPer;

    Unit(final BigDecimal pixelsPer) {
        this.pixelsPer = pixelsPer;
    }

    public BigDecimal getPixelsPer() {
        return pixelsPer;
    }
}
