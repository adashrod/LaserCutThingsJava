package com.adashrod.graphgeneration.mazes.factories;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.Direction;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;
import com.adashrod.graphgeneration.mazes.models.TopDownRectangularWallModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adashrod.graphgeneration.mazes.Direction.EAST;
import static com.adashrod.graphgeneration.mazes.Direction.NORTH;
import static com.adashrod.graphgeneration.mazes.Direction.SOUTH;
import static com.adashrod.graphgeneration.mazes.Direction.WEST;
import static java.math.BigDecimal.ZERO;

/**
 * An instance of SheetWallModelGenerator can be used to create {@link SheetWallModel}s from
 * {@link TopDownRectangularWallModel}s.
 * @author adashrod@gmail.com
 */
public class SheetWallModelGenerator {
    public static final BigDecimal PPI = new BigDecimal("90.000001");
    public static final BigDecimal DEFAULT_WALL_HEIGHT = PPI.multiply(new BigDecimal("0.5"));
    public static final BigDecimal DEFAULT_MATERIAL_THICKNESS = PPI.multiply(new BigDecimal(".118"));
    public static final BigDecimal DEFAULT_HALL_WIDTH = PPI.multiply(new BigDecimal("0.5"));;
    public static final BigDecimal DEFAULT_NOTCH_HEIGHT = PPI.multiply(new BigDecimal(".118")); // 3 mm
    public static final BigDecimal DEFAULT_SEPARATION_SPACE = PPI.multiply(new BigDecimal(".05"));;

    private final BigDecimal wallHeight;
    private final BigDecimal materialThickness;
    private final BigDecimal hallWidth;
    private final BigDecimal notchHeight;
    private final BigDecimal separationSpace;

    private final Map<SheetWallModel.Path, NotchPosInfo> notchEdgeMap = new HashMap<>();
    private final TopDownRectangularWallModel model;

    private static final Map<Direction, Integer> directionRank = new HashMap<>();
    static {
        directionRank.put(NORTH, 0);
        directionRank.put(EAST, 1);
        directionRank.put(SOUTH, 2);
        directionRank.put(WEST, 3);
    }

    public SheetWallModelGenerator(final TopDownRectangularWallModel model, final Config config) {
        this.model = model;
        this.wallHeight = config.wallHeight;
        this.materialThickness = config.materialThickness;
        this.hallWidth = config.hallWidth;
        this.notchHeight = config.notchHeight;
        this.separationSpace = config.separationSpace;
    }

    public SheetWallModel generate() {
        final SheetWallModel sheetWallModel = new SheetWallModel();

        final BigDecimal floorWidth = calcDisplacement(model.grid[0].length);
        final OrderedPair<BigDecimal> cursor = new OrderedPair<>(floorWidth.add(separationSpace), ZERO);
        for (final TopDownRectangularWallModel.Wall wall: model.walls) {
            final BigDecimal wallLength = createNotchesForWall(wall, sheetWallModel);

            // the path of the wall cutout
            final SheetWallModel.Path wallPath = new SheetWallModel.Path()
                .addPoint(new OrderedPair<>(ZERO, ZERO))
                .addPoint(new OrderedPair<>(wallLength, ZERO))
                .addPoint(new OrderedPair<>(wallLength, wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(wallLength.subtract(notchHeight), wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(wallLength.subtract(notchHeight), wallHeight))
                .addPoint(new OrderedPair<>(notchHeight, wallHeight))
                .addPoint(new OrderedPair<>(notchHeight, wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(ZERO, wallHeight.add(materialThickness)))
                .translate(cursor);
            sheetWallModel.addShape(new SheetWallModel.Shape(wallPath));
            cursor.y = cursor.y.add(wallPath.findHeight()).add(separationSpace);
        }
        createOutline(sheetWallModel);
        return sheetWallModel;
    }

    /**
     * given an index of a grid cell, calculates the physical distance to the start (left or top) of that cell from the
     * beginning of the floor
     * @param index   the grid index of the start/end cap of the wall
     * @return the left x displacement of the notch for horizontal displacements or the top y displacement for vertical
     * displacements
     */
    private BigDecimal calcDisplacement(final int index) {
        final BigDecimal mtFactor = new BigDecimal((index + 1) / 2),
            hwFactor = new BigDecimal(index / 2);
        return materialThickness.multiply(mtFactor).add(hallWidth.multiply(hwFactor));
    }

    private BigDecimal createNotchesForWall(final TopDownRectangularWallModel.Wall wall, final SheetWallModel sheetWallModel) {
        // notches in the floor for the wall tabs to fit into
        final SheetWallModel.Path firstNotch = new SheetWallModel.Path(),
            secondNotch = new SheetWallModel.Path();
        final BigDecimal wallLength;
        if (wall.getWallDirection() == EAST) {
            wallLength = calcDisplacement(wall.end.x + 1)
                .subtract(calcDisplacement(wall.start.x));
            final BigDecimal startDisplacementX = calcDisplacement(wall.start.x),
                endDisplacementX = calcDisplacement(wall.end.x + 1).subtract(notchHeight),
                displacementY = calcDisplacement(wall.start.y);
            firstNotch.addPoint(new OrderedPair<>(startDisplacementX, displacementY))
                .addPoint(new OrderedPair<>(startDisplacementX.add(notchHeight), displacementY))
                .addPoint(new OrderedPair<>(startDisplacementX.add(notchHeight), displacementY.add(materialThickness)))
                .addPoint(new OrderedPair<>(startDisplacementX, displacementY.add(materialThickness)));
            secondNotch.addPoint(new OrderedPair<>(endDisplacementX, displacementY))
                .addPoint(new OrderedPair<>(endDisplacementX.add(notchHeight), displacementY))
                .addPoint(new OrderedPair<>(endDisplacementX.add(notchHeight), displacementY.add(materialThickness)))
                .addPoint(new OrderedPair<>(endDisplacementX, displacementY.add(materialThickness)));
        } else {
            wallLength = calcDisplacement(wall.end.y + 1)
                .subtract(calcDisplacement(wall.start.y));
            final BigDecimal startDisplacementY = calcDisplacement(wall.start.y),
                endDisplacementY = calcDisplacement(wall.end.y + 1).subtract(notchHeight),
                displacementX = calcDisplacement(wall.start.x);
            firstNotch.addPoint(new OrderedPair<>(displacementX, startDisplacementY))
                .addPoint(new OrderedPair<>(displacementX.add(materialThickness), startDisplacementY))
                .addPoint(new OrderedPair<>(displacementX.add(materialThickness), startDisplacementY.add(notchHeight)))
                .addPoint(new OrderedPair<>(displacementX, startDisplacementY.add(notchHeight)));
            secondNotch.addPoint(new OrderedPair<>(displacementX, endDisplacementY))
                .addPoint(new OrderedPair<>(displacementX.add(materialThickness), endDisplacementY))
                .addPoint(new OrderedPair<>(displacementX.add(materialThickness), endDisplacementY.add(notchHeight)))
                .addPoint(new OrderedPair<>(displacementX, endDisplacementY.add(notchHeight)));
        }
        addNotchToEdgeMap(wall.start, firstNotch);
        addNotchToEdgeMap(wall.end, secondNotch);
        sheetWallModel.floorNotches.addPath(firstNotch).addPath(secondNotch);
        return wallLength;
    }
    /**
     * Each notch that touches the edge of the floor is kept in notchEdgeMap to keep track of which edge it's touching.
     * For notches that are on corner squares, they are only considered part of one edge; it is the edge that is further
     * clockwise.
     * @param wallEndCapCoords the grid-based coordinates of the notch
     * @param notch            the Path object for the notch
     */
    private void addNotchToEdgeMap(final OrderedPair<Integer> wallEndCapCoords, final SheetWallModel.Path notch) {
        final int lastRow = model.grid.length - 1, lastCol = model.grid[0].length - 1;
        if (wallEndCapCoords.y.equals(0) && !wallEndCapCoords.x.equals(lastCol)) {
            notchEdgeMap.put(notch, new NotchPosInfo(NORTH, wallEndCapCoords.x.equals(0)));
        } else if (wallEndCapCoords.x.equals(lastCol) && !wallEndCapCoords.y.equals(lastRow)) {
            notchEdgeMap.put(notch, new NotchPosInfo(EAST, wallEndCapCoords.y.equals(0)));
        } else if (wallEndCapCoords.y.equals(lastRow) && !wallEndCapCoords.x.equals(0)) {
            notchEdgeMap.put(notch, new NotchPosInfo(SOUTH, wallEndCapCoords.x.equals(lastCol)));
        } else if (wallEndCapCoords.x.equals(0) && !wallEndCapCoords.y.equals(0)) {
            notchEdgeMap.put(notch, new NotchPosInfo(WEST, wallEndCapCoords.y.equals(lastRow)));
        }
    }

    /**
     * Iterates over the edge notches and creates paths connecting them to create the vaguely rectangular outline of the
     * floor
     * @param sheetWallModel model to add paths to
     */
    private void createOutline(final SheetWallModel sheetWallModel) {
        final List<SheetWallModel.Path> paths = new ArrayList<>(notchEdgeMap.keySet());
        paths.sort(edgeNotchComparator);
        for (int i = 0; i < paths.size(); i++) {
            final SheetWallModel.Path notch = paths.get(i);
            final SheetWallModel.Path nextNotch = i < paths.size() - 1 ? paths.get(i + 1) : paths.get(0);
            final NotchPosInfo notchInfo = notchEdgeMap.get(notch), nextNotchInfo = notchEdgeMap.get(nextNotch);
            if (notchInfo.direction == nextNotchInfo.direction || nextNotchInfo.isCorner) {
                final OrderedPair<BigDecimal> firstPoint, secondPoint;
                if (notchInfo.direction == NORTH) {
                    firstPoint = notch.points.get(1);
                    secondPoint = nextNotch.points.get(0);
                } else if (notchInfo.direction == EAST) {
                    firstPoint = notch.points.get(2);
                    secondPoint = nextNotch.points.get(1);
                } else if (notchInfo.direction == SOUTH) {
                    firstPoint = notch.points.get(3);
                    secondPoint = nextNotch.points.get(2);
                } else if (notchInfo.direction == WEST) {
                    firstPoint = notch.points.get(0);
                    secondPoint = nextNotch.points.get(3);
                } else {
                    throw new IllegalStateException("notch is not in edge map, but is on edge");
                }
                if (!firstPoint.equals(secondPoint)) {
                    sheetWallModel.floorOutline.addPath(new SheetWallModel.Path(firstPoint, secondPoint).setClosed(false));
                } else {
                    System.out.println("DEBUG: skipping connecting floor outer path because it's length 0: " + firstPoint.toString());
                }
            } else { // notches are on different sides and neither is a corner (unusual case of both parts of a corner of the maze being open)
                final OrderedPair<BigDecimal> firstPoint, floorCornerPoint, secondPoint;
                if (notchInfo.direction == NORTH) {
                    firstPoint = notch.points.get(1);
                    secondPoint = nextNotch.points.get(1);
                    floorCornerPoint = new OrderedPair<>(secondPoint.x, firstPoint.y);
                } else if (notchInfo.direction == EAST) {
                    firstPoint = notch.points.get(2);
                    secondPoint = nextNotch.points.get(2);
                    floorCornerPoint = new OrderedPair<>(firstPoint.x, secondPoint.y);
                } else if (notchInfo.direction == SOUTH) {
                    firstPoint = notch.points.get(3);
                    secondPoint = nextNotch.points.get(3);
                    floorCornerPoint = new OrderedPair<>(secondPoint.x, firstPoint.y);
                } else if (notchInfo.direction == WEST) {
                    firstPoint = notch.points.get(0);
                    secondPoint = nextNotch.points.get(0);
                    floorCornerPoint = new OrderedPair<>(firstPoint.x, secondPoint.y);
                } else {
                    throw new IllegalStateException("notch is not in edge map, but is on edge");
                }
                sheetWallModel.floorOutline.addPath(new SheetWallModel.Path(firstPoint, floorCornerPoint).setClosed(false));
                sheetWallModel.floorOutline.addPath(new SheetWallModel.Path(floorCornerPoint, secondPoint).setClosed(false));
            }
        }
    }

    /**
     * sorts notches on the edge of the floor in a clockwise fashion: N,E,S,W so that the top left corner is the "lowest"
     * and a notch directly below that is the highest
     * e.g.
     * 1234
     * C  5
     * B  6
     * A987
     */
    private final Comparator<SheetWallModel.Path> edgeNotchComparator = (final SheetWallModel.Path p1, final SheetWallModel.Path p2) -> {
        final Direction p1Dir = notchEdgeMap.get(p1).direction, p2Dir = notchEdgeMap.get(p2).direction;
        final int dirCmp = Integer.compare(directionRank.get(p1Dir), directionRank.get(p2Dir));
        if (dirCmp != 0) {
            return dirCmp;
        }
        // p1Dir == p2Dir
        if (p1Dir == NORTH) {
            return p1.points.get(0).x.compareTo(p2.points.get(0).x);
        } else if (p1Dir == EAST) {
            return p1.points.get(0).y.compareTo(p2.points.get(0).y);
        } else if (p1Dir == SOUTH) {
            return p2.points.get(0).x.compareTo(p1.points.get(0).x);
        } else if (p1Dir == WEST) {
            return p2.points.get(0).y.compareTo(p1.points.get(0).y);
        } else {
            throw new IllegalStateException("notch is not in edge map, but is on edge");
        }
    };

    public static ConfigBuilder configure() {
        return new ConfigBuilder();
    }

    private static class Config {
        private final BigDecimal wallHeight;
        private final BigDecimal materialThickness;
        private final BigDecimal hallWidth;
        private final BigDecimal notchHeight;
        private final BigDecimal separationSpace;

        public Config(final BigDecimal wallHeight, final BigDecimal materialThickness, final BigDecimal hallWidth,
                final BigDecimal notchHeight, final BigDecimal separationSpace) {
            this.wallHeight = wallHeight;
            this.materialThickness = materialThickness;
            this.hallWidth = hallWidth;
            this.notchHeight = notchHeight;
            this.separationSpace = separationSpace;
        }
    }

    public static class ConfigBuilder {
        private BigDecimal wallHeight = DEFAULT_WALL_HEIGHT;
        private BigDecimal materialThickness = DEFAULT_MATERIAL_THICKNESS;
        private BigDecimal hallWidth = DEFAULT_HALL_WIDTH;
        private BigDecimal notchHeight = DEFAULT_NOTCH_HEIGHT;
        private BigDecimal separationSpace = DEFAULT_SEPARATION_SPACE;

        public ConfigBuilder withWallHeight(final BigDecimal wallHeight) {
            this.wallHeight = wallHeight;
            return this;
        }

        public ConfigBuilder withMaterialThickness(final BigDecimal materialThickness) {
            this.materialThickness = materialThickness;
            return this;
        }

        public ConfigBuilder withHallWidth(final BigDecimal hallWidth) {
            this.hallWidth = hallWidth;
            return this;
        }

        public ConfigBuilder withNotchHeight(final BigDecimal notchHeight) {
            this.notchHeight = notchHeight;
            return this;
        }

        public ConfigBuilder withSeparationSpace(final BigDecimal separationSpace) {
            this.separationSpace = separationSpace;
            return this;
        }

        public Config build() {
            final StringBuilder errors = new StringBuilder();
            if (wallHeight.compareTo(ZERO) <= 0) {
                errors.append("wallHeight must be positive; ");
            }
            if (materialThickness.compareTo(ZERO) <= 0) {
                errors.append("materialThickness must be positive; ");
            }
            if (hallWidth.compareTo(ZERO) <= 0) {
                errors.append("hallWidth must be positive; ");
            }
            if (separationSpace.compareTo(ZERO) <= 0) {
                errors.append("separationSpace must be positive; ");
            }
            if (notchHeight.multiply(new BigDecimal(2)).compareTo(hallWidth) >= 0) {
                errors.append("notchHeight must be less than half of hallWidth; ");
            }
            if (errors.length() > 0) {
                errors.delete(errors.length() - 2, errors.length());
                throw new IllegalArgumentException(errors.toString());
            }
            return new Config(wallHeight, materialThickness, hallWidth, notchHeight, separationSpace);
        }
    }

    private static final class NotchPosInfo {
        private final Direction direction;
        private final boolean isCorner;

        private NotchPosInfo(final Direction direction, final boolean isCorner) {
            this.direction = direction;
            this.isCorner = isCorner;
        }
    }
}
