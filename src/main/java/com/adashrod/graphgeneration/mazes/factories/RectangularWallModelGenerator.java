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
    private final boolean[][] isWall;

    public RectangularWallModelGenerator(final LinearWallModel model) {
        this.model = model;
        isWall = new boolean[2 * model.height + 1][2 * model.width + 1];
    }

    public RectangularWallModel generate() {
        final RectangularWallModel rectangularWallModel = new RectangularWallModel(isWall[0].length, isWall.length);

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
        final Direction endDirection = wallsAreVertical ? SOUTH : EAST;
        walls.forEach((final LinearWallModel.Wall wall) -> {
            int wsx = wall.start.x * 2, wex = wall.end.x * 2, wsy = wall.start.y * 2, wey = wall.end.y * 2;
            if (!isFirstSetOfWalls) {
                if (wallsAreVertical) {
                    if (isWall[wsy][wsx]) { wsy++; }
                    if (isWall[wey][wex]) { wey--; }
                } else {
                    if (isWall[wsy][wsx]) { wsx++; }
                    if (isWall[wey][wex]) { wex--; }
                }
            }
            final RectangularWallModel.Wall rectWall = new RectangularWallModel.Wall(new OrderedPair<>(wsx, wsy),
                new OrderedPair<>(wex, wey), endDirection);
            rectangularWallModel.addWall(rectWall);
            fillOutWallSpaces(wallsAreVertical, wsx, wsy, wex, wey);
        });
    }

    private void fillOutWallSpaces(final boolean wallsAreVertical, final int wsx, final int wsy, final int wex,
            final int wey) {
        if (wallsAreVertical) {
            for (int y = wsy; y <= wey; y++) {
                isWall[y][wsx] = true;
            }
        } else {
            for (int x = wsx; x <= wex; x++) {
                isWall[wsy][x] = true;
            }
        }

    }
}
