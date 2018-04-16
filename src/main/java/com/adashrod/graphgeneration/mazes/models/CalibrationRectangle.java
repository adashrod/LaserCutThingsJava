package com.adashrod.graphgeneration.mazes.models;

import com.adashrod.graphgeneration.common.Unit;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

/**
 * @author adashrod@gmail.com
 */
public class CalibrationRectangle {
    private final int width;
    private final int height;
    private final Unit unit;
    private final boolean isLeftAligned;
    private final boolean isTopAligned;
    private final BigDecimal maxWidth;
    private final BigDecimal maxHeight;

    CalibrationRectangle(final int width, final int height, final Unit unit,
            final boolean isLeftAligned, final boolean isTopAligned, final BigDecimal maxWidth, final BigDecimal maxHeight) {
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.isLeftAligned = isLeftAligned;
        this.isTopAligned = isTopAligned;
        this.maxWidth = maxWidth.multiply(unit.getPixelsPer());
        this.maxHeight = maxHeight.multiply(unit.getPixelsPer());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isLeftAligned() {
        return isLeftAligned;
    }

    public boolean isTopAligned() {
        return isTopAligned;
    }

    public BigDecimal getMaxWidth() {
        return maxWidth;
    }

    public BigDecimal getMaxHeight() {
        return maxHeight;
    }

    public static Builder configure() {
        return new Builder();
    }

    public static class Builder {
        private int width;
        private int height;
        private Unit unit;
        private boolean isLeftAligned;
        private boolean isTopAligned;
        private BigDecimal maxWidth;
        private BigDecimal maxHeight;

        public Builder withWidth(final int width) {
            this.width = width;
            return this;
        }

        public Builder withHeight(final int height) {
            this.height = height;
            return this;
        }

        public Builder withUnit(final Unit unit) {
            this.unit = unit;
            return this;
        }

        public Builder withLeftAlignment() {
            isLeftAligned = true;
            return this;
        }

        public Builder withRightAlignment() {
            isLeftAligned = false;
            return this;
        }

        public Builder withTopAlignment() {
            isTopAligned = true;
            return this;
        }

        public Builder withBottomAlignment() {
            isTopAligned = false;
            return this;
        }

        public Builder withMaxWidth(final BigDecimal maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder withMaxHeight(final BigDecimal maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public CalibrationRectangle build() {
            final StringBuilder errors = new StringBuilder();
            if (unit != Unit.CENTIMETERS && unit != Unit.INCHES) {
                errors.append("unit must be either INCHES or CENTIMETERS; ");
            }
            if (width <= 0) {
                errors.append("width must be positive; ");
            }
            if (height <= 0) {
                errors.append("height must be positive; ");
            }
            if (maxWidth.compareTo(ZERO) <= 0) {
                errors.append("maxWidth must be positive; ");
            }
            if (maxHeight.compareTo(ZERO) <= 0) {
                errors.append("maxHeight must be positive; ");
            }
            if (errors.length() > 0) {
                errors.delete(errors.length() - 2, errors.length());
                throw new IllegalArgumentException(errors.toString());
            }
            return new CalibrationRectangle(width, height, unit, isLeftAligned, isTopAligned, maxWidth, maxHeight);
        }
    }
}
