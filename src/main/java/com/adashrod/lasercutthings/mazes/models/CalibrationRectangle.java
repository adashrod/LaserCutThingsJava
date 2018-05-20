package com.adashrod.lasercutthings.mazes.models;

import com.adashrod.lasercutthings.common.Unit;

/**
 * A model for a single rectangle with integral width and height values in inches or cm. This can be printed on an SVG
 * to help calibrate the scale of the SVG on printing software.
 * @author adashrod@gmail.com
 */
public class CalibrationRectangle {
    private final int width;
    private final int height;
    private final Unit unit;
    private final boolean isLeftAligned;
    private final boolean isTopAligned;

    CalibrationRectangle(final int width, final int height, final Unit unit, final boolean isLeftAligned,
            final boolean isTopAligned) {
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.isLeftAligned = isLeftAligned;
        this.isTopAligned = isTopAligned;
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

    public static Builder configure() {
        return new Builder();
    }

    public static class Builder {
        private int width;
        private int height;
        private Unit unit;
        private boolean isLeftAligned;
        private boolean isTopAligned;

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
            if (errors.length() > 0) {
                errors.delete(errors.length() - 2, errors.length());
                throw new IllegalArgumentException(errors.toString());
            }
            return new CalibrationRectangle(width, height, unit, isLeftAligned, isTopAligned);
        }
    }
}
