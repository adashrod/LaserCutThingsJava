package com.adashrod.graphgeneration.svg;

import com.adashrod.graphgeneration.common.OrderedPair;
import com.adashrod.graphgeneration.mazes.models.SheetWallModel;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * todo
 * @author adashrod@gmail.com
 */
public class SvgElementGenerator {
    public Path sheetPathToSvgPath(final SheetWallModel.Path path) {
        final Path result = new Path();
        result.multiPartPath.addAll(path.points);
        if (path.isClosed) {
            result.multiPartPath.add(path.points.get(0));
        }
        return result;
    }

    public String pathToSvgText(final Path path, final int fpPrecision) {
        final MathContext mc = new MathContext(fpPrecision);
        final StringBuilder dAttrBuilder = new StringBuilder();
        dAttrBuilder.append("M");
        if (path.multiPartPath.isEmpty()) {
            dAttrBuilder.append(" ")
                .append(path.start.x.round(mc).stripTrailingZeros()).append(",")
                .append(path.start.y.round(mc).stripTrailingZeros())
                .append(" ")
                .append(path.end.x.round(mc).stripTrailingZeros()).append(",")
                .append(path.end.y.round(mc).stripTrailingZeros());
        } else {
            path.multiPartPath.forEach((final OrderedPair<BigDecimal> point) -> {
                dAttrBuilder.append(" ").append(point.x.round(mc).stripTrailingZeros()).append(",")
                    .append(point.y.round(mc).stripTrailingZeros());
            });
            if (path.multiPartPath.get(0).equals(path.multiPartPath.get(path.multiPartPath.size() - 1))) {
                dAttrBuilder.append(" Z"); // closed path
            }
        }
        return String.format("<path style=\"%s\" d=\"%s\" id=\"%s\"/>", path.style, dAttrBuilder.toString(), path.id != null ? path.id : "");
    }
}
