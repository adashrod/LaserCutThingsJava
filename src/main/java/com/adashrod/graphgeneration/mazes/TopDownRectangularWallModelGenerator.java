package com.adashrod.graphgeneration.mazes;

import java.util.ArrayList;
import java.util.Collection;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.NORTH;
import static com.adashrod.graphgeneration.mazes.Direction.SOUTH;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;
import static com.adashrod.graphgeneration.mazes.Direction.determineDirection;

/**
 * An instance of TopDownRectangularWallModelGenerator can be used to create a {@link TopDownRectangularWallModel} from
 * a {@link LinearWallModel}
 * @author adashrod@gmail.com
 */
public class TopDownRectangularWallModelGenerator {
    private final LinearWallModel model;

    public TopDownRectangularWallModelGenerator(final LinearWallModel model) {
        this.model = model;
    }

    public TopDownRectangularWallModel generate() {
        final TopDownRectangularWallModel topDownRectangularWallModel = new TopDownRectangularWallModel(2 * model.width + 1, 2 * model.height + 1);

        final Collection<LinearWallModel.Wall> verticalWalls = new ArrayList<>(), horizontalWalls = new ArrayList<>();
        model.walls.forEach((final LinearWallModel.Wall wall) -> {
            final Direction wallDirection = determineDirection(wall.start, wall.end);
            if (wallDirection == NORTH || wallDirection == WEST) {
                throw new IllegalStateException("wall direction should only be EAST or SOUTH (start-to-end should be left-to-right or top-to-bottom): " +
                    wall.toString());
            }
            final boolean isVertical = wallDirection == SOUTH;
            if (isVertical) {
                verticalWalls.add(wall);
            } else {
                horizontalWalls.add(wall);
            }
        });
        if (model.favorEwWalls) {
            createWallSpacesFromLinearWalls(topDownRectangularWallModel, horizontalWalls, false, true);
            createWallSpacesFromLinearWalls(topDownRectangularWallModel, verticalWalls, true, false);
        } else {
            createWallSpacesFromLinearWalls(topDownRectangularWallModel, verticalWalls, true, true);
            createWallSpacesFromLinearWalls(topDownRectangularWallModel, horizontalWalls, false, false);
        }

        return topDownRectangularWallModel;
    }

    private void createWallSpacesFromLinearWalls(final TopDownRectangularWallModel topDownRectangularWallModel,
            final Iterable<LinearWallModel.Wall> walls, final boolean wallsAreVertical, final boolean isFirstSetOfWalls) {
        final Direction startDirection, endDirection;
        if (wallsAreVertical) {
            startDirection = NORTH;
            endDirection = SOUTH;
        } else {
            startDirection = WEST;
            endDirection = EAST;
        }
        walls.forEach((final LinearWallModel.Wall wall) -> {
            int wsx = wall.start.x * 2, wex = wall.end.x * 2, wsy = wall.start.y * 2, wey = wall.end.y * 2;
            if (!isFirstSetOfWalls) {
                if (wallsAreVertical) {
                    if (topDownRectangularWallModel.grid[wsy][wsx].isWall) { wsy++; }
                    if (topDownRectangularWallModel.grid[wey][wex].isWall) { wey--; }
                } else {
                    if (topDownRectangularWallModel.grid[wsy][wsx].isWall) { wsx++; }
                    if (topDownRectangularWallModel.grid[wey][wex].isWall) { wex--; }
                }
            }
            topDownRectangularWallModel.grid[wsy][wsx].endDirections.add(startDirection);
            topDownRectangularWallModel.grid[wey][wex].endDirections.add(endDirection);
            if (wallsAreVertical) {
                for (int y = wsy; y <= wey; y++) {
                    topDownRectangularWallModel.grid[y][wsx].isWall = true;
                }
            } else {
                for (int x = wsx; x <= wex; x++) {
                    topDownRectangularWallModel.grid[wsy][x].isWall = true;
                }
            }
        });
    }
}
