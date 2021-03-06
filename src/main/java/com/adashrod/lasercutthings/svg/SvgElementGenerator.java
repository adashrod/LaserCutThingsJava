package com.adashrod.lasercutthings.svg;

import com.adashrod.lasercutthings.common.OrderedPair;
import com.adashrod.lasercutthings.mazes.models.Shape;
import com.adashrod.lasercutthings.mazes.models.VectorNumber;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.adashrod.lasercutthings.mazes.models.VectorNumber.CHARACTER_HEIGHT;
import static com.adashrod.lasercutthings.mazes.models.VectorNumber.CHARACTER_WIDTH;
import static java.math.BigDecimal.ZERO;

/**
 * A simple, not very flexible utility for serializing SVG elements to strings for manual SVG file construction.
 * To be replaced by an SVG library later.
 * @author adashrod@gmail.com
 */
public class SvgElementGenerator {
    public Path modelPathToSvgPath(final com.adashrod.lasercutthings.mazes.models.Path path) {
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

    public String circleToSvgText(final Circle circle, final int fpPrecision) {
        final MathContext mc = new MathContext(fpPrecision);
        return String.format("<circle cx=\"%s\" cy=\"%s\" r=\"%s\" stroke=\"black\" stroke-width=\"1\" fill=\"none\" />",
            circle.center.x.round(mc), circle.center.y.round(mc), circle.radius.round(mc));
    }

    public String rectToSvgText(final Rect rect, final int fpPrecision) {
        final MathContext mc = new MathContext(fpPrecision);
        return String.format("<rect style=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\"/>",
            rect.style != null ? rect.style : "",
            rect.x.round(mc), rect.y.round(mc), rect.width.round(mc), rect.height.round(mc));
    }

    public String vectorNumberToSvgText(final VectorNumber vectorNumber, final int fpPrecision) {
        final MathContext mc = new MathContext(fpPrecision);
        final String vnStr = String.valueOf(vectorNumber.number);
        final Shape stringShape = new Shape();
        for (int i = 0; i < vnStr.length(); i++) {
            final char c = vnStr.charAt(i);
            final Shape charShape = VectorNumber.characterToShape(c);
            final BigDecimal currentWidth = new BigDecimal(i * CHARACTER_WIDTH);
            charShape.translate(new OrderedPair<>(currentWidth, ZERO));
            stringShape.addShape(charShape);
        }
        stringShape.scale(new OrderedPair<>(vectorNumber.width.divide(new BigDecimal(CHARACTER_WIDTH * vnStr.length()), mc),
            vectorNumber.height.divide(new BigDecimal(CHARACTER_HEIGHT), mc))).translate(vectorNumber.position);
        final StringBuilder svgTextBuilder = new StringBuilder();
        stringShape.paths.forEach((final com.adashrod.lasercutthings.mazes.models.Path path) -> {
            final Path svgPath = modelPathToSvgPath(path);
            svgPath.style = svgPath.style.replace("000000", "0000ff");
            svgTextBuilder.append(pathToSvgText(svgPath, fpPrecision));
        });
        return svgTextBuilder.toString();
    }
}
