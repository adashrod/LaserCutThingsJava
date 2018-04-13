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
    private final Space[][] grid;

    public RectangularWallModelGenerator(final LinearWallModel model) {
        this.model = model;
        grid = new Space[2 * model.width + 1][2 * model.height + 1];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x] = new Space();
            }
        }
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
        final Direction endDirection = wallsAreVertical ? SOUTH : EAST;
        walls.forEach((final LinearWallModel.Wall wall) -> {
            int wsx = wall.start.x * 2, wex = wall.end.x * 2, wsy = wall.start.y * 2, wey = wall.end.y * 2;
            if (!isFirstSetOfWalls) {
                if (wallsAreVertical) {
                    if (grid[wsy][wsx].isWall) { wsy++; }
                    if (grid[wey][wex].isWall) { wey--; }
                } else {
                    if (grid[wsy][wsx].isWall) { wsx++; }
                    if (grid[wey][wex].isWall) { wex--; }
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
                grid[y][wsx].isWall = true;
            }
        } else {
            for (int x = wsx; x <= wex; x++) {
                grid[wsy][x].isWall = true;
            }
        }

    }

    private static class Space {
        public boolean isWall;
        public final Collection<Direction> endDirections = new ArrayList<>(); // todo: get rid of direct access

        @Override
        public String toString() {
            return String.format("Space[%s,%s]", isWall ? "#" : " ", endDirections.isEmpty() ? "" : endDirections);
        }
    }
}
