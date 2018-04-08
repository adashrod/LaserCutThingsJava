package com.adashrod.graphgeneration.mazes.factories;

import com.adashrod.graphgeneration.mazes.models.SheetWallModel;
import com.adashrod.graphgeneration.mazes.models.TopDownRectangularWallModel;

/**
 * Created by adashrod@gmail.com on 2018-04-07.
 */
public class SheetWallModelGenerator {
    private final TopDownRectangularWallModel model;

    public SheetWallModelGenerator(final TopDownRectangularWallModel model) {
        this.model = model;
    }

    public SheetWallModel generate() {
        final SheetWallModel sheetWallModel = new SheetWallModel();

        // blah blah blah

        return sheetWallModel;
    }
}
