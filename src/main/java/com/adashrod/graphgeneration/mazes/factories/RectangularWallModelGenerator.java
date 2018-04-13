package com.adashrod.graphgeneration.mazes.factories;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.Direction;
import com.adashrod.graphgeneration.mazes.models.LinearWallModel;
import com.adashrod.graphgeneration.mazes.models.RectangularWallModel;

import java.util.ArrayList;
import java.util.Collection;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.NORTH;
import static com.adashrod.graphgeneration.mazes.Direction.SOUTH;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;
import static com.adashrod.graphgeneration.mazes.Direction.determineDirection;

/**
 * An instance of RectangularWallModelGenerator can be used to create a {@link RectangularWallModel} from
 * a {@link LinearWallModel}
 * @author adashrod@gmail.com
 */
public class RectangularWallModelGenerator {
    private final LinearWallModel model;

    public RectangularWallModelGenerator(final LinearWallModel model) {
        this.model = model;
    }

    public RectangularWallModel generate() {
        final RectangularWallModel rectangularWallModel = new RectangularWallModel(2 * model.width + 1, 2 * model.height + 1);

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
            createWallSpacesFromLinearWalls(rectangularWallModel, horizontalWalls, false, true);
            createWallSpacesFromLinearWalls(rectangularWallModel, verticalWalls, true, false);
        } else {
            createWallSpacesFromLinearWalls(rectangularWallModel, verticalWalls, true, true);
            createWallSpacesFromLinearWalls(rectangularWallModel, horizontalWalls, false, false);
        }

        return rectangularWallModel;
    }

    private void createWallSpacesFromLinearWalls(final RectangularWallModel rectangularWallModel,
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
                    if (rectangularWallModel.grid[wsy][wsx].isWall) { wsy++; }
                    if (rectangularWallModel.grid[wey][wex].isWall) { wey--; }
                } else {
                    if (rectangularWallModel.grid[wsy][wsx].isWall) { wsx++; }
                    if (rectangularWallModel.grid[wey][wex].isWall) { wex--; }
                }
            }
            // todo: consider whether to have all of the following statements in the model setters or here (anemic domain model)
            final RectangularWallModel.Wall tdrWall = new RectangularWallModel.Wall(new OrderedPair<>(wsx, wsy),
                new OrderedPair<>(wex, wey), endDirection);
            rectangularWallModel.addWall(tdrWall);
            rectangularWallModel.grid[wsy][wsx].endDirections.add(startDirection);
            rectangularWallModel.grid[wey][wex].endDirections.add(endDirection);
            if (wallsAreVertical) {
                for (int y = wsy; y <= wey; y++) {
                    rectangularWallModel.grid[y][wsx].isWall = true;
                }
            } else {
                for (int x = wsx; x <= wex; x++) {
                    rectangularWallModel.grid[wsy][x].isWall = true;
                }
            }
        });
    }
}
