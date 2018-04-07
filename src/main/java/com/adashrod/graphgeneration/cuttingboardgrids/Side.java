package com.adashrod.graphgeneration.cuttingboardgrids;

/**
 * Created by aaron on 2018-04-01.
 */
public enum Side {
    TOP(false, false),
    RIGHT(true, true), // assuming that flip happens before transpose
    BOTTOM(false, true),
    LEFT(true, false);

    final boolean transpose;
    final boolean flip;

    Side(final boolean transpose, final boolean flip) {
        this.transpose = transpose;
        this.flip = flip;
    }
}
