package com.adashrod.graphgeneration.mazes.factories;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.Direction;
import com.adashrod.graphgeneration.mazes.models.Path;
import com.adashrod.graphgeneration.mazes.models.RectangularWallModel;
import com.adashrod.graphgeneration.mazes.models.Shape;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;
import com.adashrod.graphgeneration.mazes.models.VectorNumber;

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
 * {@link RectangularWallModel}s.
 * @author adashrod@gmail.com
 */
public class SheetWallModelGenerator {
    public static final BigDecimal PPI = new BigDecimal("90.000001");
    public static final BigDecimal PPMM = new BigDecimal("3.5433071");
    public static final BigDecimal DEFAULT_WALL_HEIGHT = new BigDecimal("0.5");
    public static final BigDecimal DEFAULT_MATERIAL_THICKNESS = new BigDecimal(".118");
    public static final BigDecimal DEFAULT_HALL_WIDTH = new BigDecimal("0.5");
    public static final BigDecimal DEFAULT_NOTCH_HEIGHT = new BigDecimal(".118");
    public static final BigDecimal DEFAULT_SEPARATION_SPACE = new BigDecimal(".05");
    public static final BigDecimal DEFAULT_MAX_WIDTH = new BigDecimal("19.5");
    public static final BigDecimal DEFAULT_MAX_HEIGHT = new BigDecimal("11");

    private final BigDecimal wallHeight;
    private final BigDecimal materialThickness;
    private final BigDecimal hallWidth;
    private final BigDecimal notchHeight;
    private final BigDecimal separationSpace;
    private final BigDecimal maxWidth;
    private final BigDecimal maxHeight;

    private final Map<Path, NotchPosInfo> notchEdgeMap = new HashMap<>();
    private final RectangularWallModel model;
    private final Map<BigDecimal, Integer> wallTypeLabelsByLength = new HashMap<>();

    private static final Map<Direction, Integer> directionRank = new HashMap<>();
    static {
        directionRank.put(NORTH, 0);
        directionRank.put(EAST, 1);
        directionRank.put(SOUTH, 2);
        directionRank.put(WEST, 3);
    }

    public SheetWallModelGenerator(final RectangularWallModel model, final Config config) {
        this.model = model;
        this.wallHeight = config.wallHeight;
        this.materialThickness = config.materialThickness;
        this.hallWidth = config.hallWidth;
        this.notchHeight = config.notchHeight;
        this.separationSpace = config.separationSpace;
        this.maxWidth = config.maxWidth;
        this.maxHeight = config.maxHeight;
    }

    public SheetWallModel generate() {
        final SheetWallModel sheetWallModel = new SheetWallModel();

        // for now, all walls and the floor will be positioned at (0,0). They'll be translated and tiled on the
        // print sheet later
        final List<RectangularWallModel.Wall> sortedWalls = new ArrayList<>(model.walls);
        sortedWalls.sort(Comparator.comparing(wall -> wall.length));
        for (final RectangularWallModel.Wall wall: sortedWalls) {
            final BigDecimal wallLength = createNotchesForWall(wall, sheetWallModel);
            final Path wallPath = new Path()
                .addPoint(new OrderedPair<>(ZERO, ZERO))
                .addPoint(new OrderedPair<>(wallLength, ZERO))
                .addPoint(new OrderedPair<>(wallLength, wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(wallLength.subtract(notchHeight), wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(wallLength.subtract(notchHeight), wallHeight))
                .addPoint(new OrderedPair<>(notchHeight, wallHeight))
                .addPoint(new OrderedPair<>(notchHeight, wallHeight.add(materialThickness)))
                .addPoint(new OrderedPair<>(ZERO, wallHeight.add(materialThickness)));
            final Shape wallShape = new Shape(wallPath);
            sheetWallModel.addShape(wallShape);
            final int wallTypeLabel = findWallTypeLabel(wallLength);
            final BigDecimal vnHeight = wallHeight.multiply(new BigDecimal(".5")),
                vnWidth = vnHeight.multiply(new BigDecimal(".5")).multiply(new BigDecimal(numDigits(wallTypeLabel)));
            sheetWallModel.wallLabels.put(wallShape, new VectorNumber(wallTypeLabel, vnWidth.min(wallLength), vnHeight,
                new OrderedPair<>(ZERO, ZERO))); // translate in optimizer
        }
        createOutline(sheetWallModel);
        new SheetWallTilingOptimizer(sheetWallModel, separationSpace, maxWidth, maxHeight).optimize();
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

    private BigDecimal createNotchesForWall(final RectangularWallModel.Wall wall, final SheetWallModel sheetWallModel) {
        final BigDecimal half = new BigDecimal(".5");
        // notches in the floor for the wall tabs to fit into
        final Path firstNotch = new Path(),
            secondNotch = new Path();
        final BigDecimal wallLength;
        final VectorNumber vectorNumber;
        if (wall.getWallDirection() == EAST) {
            wallLength = calcDisplacement(wall.end.x + 1)
                .subtract(calcDisplacement(wall.start.x));
            final int wallTypeLabel = findWallTypeLabel(wallLength);
            final BigDecimal startDisplacementX = calcDisplacement(wall.start.x),
                endDisplacementX = calcDisplacement(wall.end.x + 1).subtract(notchHeight),
                displacementY = calcDisplacement(wall.start.y);
            final BigDecimal spaceBetweenNotches = endDisplacementX.subtract(startDisplacementX).subtract(notchHeight);
            BigDecimal vnWidth = materialThickness.multiply(half).multiply(new BigDecimal(numDigits(wallTypeLabel)));
            vnWidth = vnWidth.min(spaceBetweenNotches);
            vectorNumber = new VectorNumber(wallTypeLabel, vnWidth, materialThickness, new OrderedPair<>(
                startDisplacementX.add(notchHeight).add(spaceBetweenNotches.multiply(half).subtract(vnWidth.multiply(half))), displacementY));
            sheetWallModel.floorNumbers.add(vectorNumber);
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
            final int wallTypeLabel = findWallTypeLabel(wallLength);
            final BigDecimal startDisplacementY = calcDisplacement(wall.start.y),
                endDisplacementY = calcDisplacement(wall.end.y + 1).subtract(notchHeight),
                displacementX = calcDisplacement(wall.start.x);
            final BigDecimal spaceBetweenNotches = endDisplacementY.subtract(startDisplacementY).subtract(notchHeight);
            BigDecimal vnWidth = materialThickness.multiply(half).multiply(new BigDecimal(numDigits(wallTypeLabel)));
            vnWidth = vnWidth.min(materialThickness);
            vectorNumber = new VectorNumber(wallTypeLabel, vnWidth, materialThickness, new OrderedPair<>(displacementX.add(materialThickness.multiply(half)).subtract(vnWidth.multiply(half)),
                startDisplacementY.add(notchHeight).add(spaceBetweenNotches.multiply(half).subtract(materialThickness.multiply(half))))); // adjust the xPos of the vn to be centered
            sheetWallModel.floorNumbers.add(vectorNumber);
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
    private void addNotchToEdgeMap(final OrderedPair<Integer> wallEndCapCoords, final Path notch) {
        final int lastRow = model.height - 1, lastCol = model.width - 1;
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
        final List<Path> paths = new ArrayList<>(notchEdgeMap.keySet());
        paths.sort(edgeNotchComparator);
        for (int i = 0; i < paths.size(); i++) {
            final Path notch = paths.get(i);
            final Path nextNotch = i < paths.size() - 1 ? paths.get(i + 1) : paths.get(0);
            final NotchPosInfo notchInfo = notchEdgeMap.get(notch), nextNotchInfo = notchEdgeMap.get(nextNotch);
            if (notchInfo.direction == nextNotchInfo.direction || nextNotchInfo.isCorner) {
                final NotchConnection points = findNotchConnectionPoints(notchInfo, notch, nextNotch, false);
                final OrderedPair<BigDecimal> firstPoint = points.firstPoint, secondPoint = points.secondPoint;
                if (!firstPoint.equals(secondPoint)) {
                    sheetWallModel.floorOutline.addPath(new Path(firstPoint, secondPoint).setClosed(false));
                } else {
                    System.out.println("DEBUG: skipping connecting floor outer path because it's length 0: " + firstPoint.toString());
                }
            } else { // notches are on different sides and neither is a corner (unusual case of both parts of a corner of the maze being open)
                final NotchConnection points = findNotchConnectionPoints(notchInfo, notch, nextNotch, true);
                sheetWallModel.floorOutline.addPath(new Path(points.firstPoint, points.cornerPoint).setClosed(false));
                sheetWallModel.floorOutline.addPath(new Path(points.cornerPoint, points.secondPoint).setClosed(false));
            }
        }
    }

    private NotchConnection findNotchConnectionPoints(final NotchPosInfo notchInfo,
            final Path notch, final Path nextNotch, final boolean includeCorner) {
        final int nextNotchAdditive = includeCorner ? 1 : 0;
        final OrderedPair<BigDecimal> firstPoint, floorCornerPoint, secondPoint;
        if (notchInfo.direction == NORTH) {
            firstPoint = notch.points.get(1);
            secondPoint = nextNotch.points.get(nextNotchAdditive);
            floorCornerPoint = new OrderedPair<>(secondPoint.x, firstPoint.y);
        } else if (notchInfo.direction == EAST) {
            firstPoint = notch.points.get(2);
            secondPoint = nextNotch.points.get(1 + nextNotchAdditive);
            floorCornerPoint = new OrderedPair<>(firstPoint.x, secondPoint.y);
        } else if (notchInfo.direction == SOUTH) {
            firstPoint = notch.points.get(3);
            secondPoint = nextNotch.points.get(2 + nextNotchAdditive);
            floorCornerPoint = new OrderedPair<>(secondPoint.x, firstPoint.y);
        } else if (notchInfo.direction == WEST) {
            firstPoint = notch.points.get(0);
            secondPoint = nextNotch.points.get(3 + nextNotchAdditive % 4);
            floorCornerPoint = new OrderedPair<>(firstPoint.x, secondPoint.y);
        } else {
            throw new IllegalStateException("notch is not in edge map, but is on edge");
        }
        return new NotchConnection(firstPoint, floorCornerPoint, secondPoint);
    }

    private int numDigits(final long number) {
        final long n = number < 0 ? -1 * number : number;
        int exp = 1;
        int powerOfTen = 10;
        while (true) {
            if (n < powerOfTen) {
                return exp;
            }
            powerOfTen *= 10;
            exp++;
        }
    }

    private int findWallTypeLabel(final BigDecimal wallLength) {
        Integer label = wallTypeLabelsByLength.get(wallLength);
        if (label != null) {
            return label;
        }
        label = wallTypeLabelsByLength.size();
        wallTypeLabelsByLength.put(wallLength, label);
        return label;
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
    private final Comparator<Path> edgeNotchComparator = (final Path p1, final Path p2) -> {
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

    public static class Config {
        // all of these values are in px, not in or cm
        private final BigDecimal wallHeight;
        private final BigDecimal materialThickness;
        private final BigDecimal hallWidth;
        private final BigDecimal notchHeight;
        private final BigDecimal separationSpace;
        private final BigDecimal maxWidth;
        private final BigDecimal maxHeight;

        public Config(final Unit unit, final BigDecimal wallHeight, final BigDecimal materialThickness,
                final BigDecimal hallWidth, final BigDecimal notchHeight, final BigDecimal separationSpace,
                final BigDecimal maxWidth, final BigDecimal maxHeight) {
            if (unit == Unit.INCHES) {
                this.wallHeight = wallHeight.multiply(PPI);
                this.materialThickness = materialThickness.multiply(PPI);
                this.hallWidth = hallWidth.multiply(PPI);
                this.notchHeight = notchHeight.multiply(PPI);
                this.separationSpace = separationSpace.multiply(PPI);
                this.maxWidth = maxWidth.multiply(PPI);
                this.maxHeight = maxHeight.multiply(PPI);
            } else {
                this.wallHeight = wallHeight.multiply(PPMM);
                this.materialThickness = materialThickness.multiply(PPMM);
                this.hallWidth = hallWidth.multiply(PPMM);
                this.notchHeight = notchHeight.multiply(PPMM);
                this.separationSpace = separationSpace.multiply(PPMM);
                this.maxWidth = maxWidth.multiply(PPMM);
                this.maxHeight = maxHeight.multiply(PPMM);
            }
        }

        public enum Unit {
            INCHES,
            MILLIMETERS
        }
    }

    public static class ConfigBuilder {
        private Config.Unit unit = Config.Unit.INCHES;
        private BigDecimal wallHeight = DEFAULT_WALL_HEIGHT;
        private BigDecimal materialThickness = DEFAULT_MATERIAL_THICKNESS;
        private BigDecimal hallWidth = DEFAULT_HALL_WIDTH;
        private BigDecimal notchHeight = DEFAULT_NOTCH_HEIGHT;
        private BigDecimal separationSpace = DEFAULT_SEPARATION_SPACE;
        private BigDecimal maxWidth = DEFAULT_MAX_WIDTH;
        private BigDecimal maxHeight = DEFAULT_MAX_HEIGHT;

        /**
         * sets preferred units for supplying values to the other setters in this class. Whichever is chosen, values
         * will ultimately get converted to pixels.
         * @param unit inches or cm.
         * @return this
         */
        public ConfigBuilder withUnit(final Config.Unit unit) {
            this.unit = unit;
            return this;
        }

        /**
         * sets height of the walls
         * @param wallHeight new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder withWallHeight(final BigDecimal wallHeight) {
            this.wallHeight = wallHeight;
            return this;
        }

        /**
         * sets thickness of material being used to print. This will also therefore be the width of the walls.
         * @param materialThickness new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder withMaterialThickness(final BigDecimal materialThickness) {
            this.materialThickness = materialThickness;
            return this;
        }

        /**
         * sets width between walls
         * @param hallWidth new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder withHallWidth(final BigDecimal hallWidth) {
            this.hallWidth = hallWidth;
            return this;
        }

        /**
         * sets height of the floor notches. This dimension is parallel to the walls
         * @param notchHeight new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder withNotchHeight(final BigDecimal notchHeight) {
            this.notchHeight = notchHeight;
            return this;
        }

        /**
         * sets minimum space between vectors
         * @param separationSpace new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder withSeparationSpace(final BigDecimal separationSpace) {
            this.separationSpace = separationSpace;
            return this;
        }

        /**
         * sets max width of the SVG so that vectors are contained within an area
         * @param maxWidth new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder setMaxWidth(final BigDecimal maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * sets max height of the SVG so that vectors are contained within an area
         * @param maxHeight new value in the unit specified in {@link ConfigBuilder#withUnit(Config.Unit)}
         * @return this
         */
        public ConfigBuilder setMaxHeight(final BigDecimal maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Config build() {
            final StringBuilder errors = new StringBuilder();
            if (unit == null) {
                errors.append("unit must not be null; ");
            }
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
            return new Config(unit, wallHeight, materialThickness, hallWidth, notchHeight, separationSpace, maxWidth,
                maxHeight);
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

    private static final class NotchConnection {
        private final OrderedPair<BigDecimal> firstPoint;
        private final OrderedPair<BigDecimal> cornerPoint;
        private final OrderedPair<BigDecimal> secondPoint;

        private NotchConnection(final OrderedPair<BigDecimal> firstPoint, final OrderedPair<BigDecimal> cornerPoint,
                final OrderedPair<BigDecimal> secondPoint) {
            this.firstPoint = firstPoint;
            this.cornerPoint = cornerPoint;
            this.secondPoint = secondPoint;
        }
    }
}
