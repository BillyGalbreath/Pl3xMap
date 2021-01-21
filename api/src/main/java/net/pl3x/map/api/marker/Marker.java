package net.pl3x.map.api.marker;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.Point;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Parent class to all map markers, also contains static factory methods for different marker types
 */
public abstract class Marker {

    private MarkerOptions markerOptions = MarkerOptions.defaultOptions();

    protected Marker() {
    }

    /**
     * Get the current marker options used by this marker
     *
     * @return options
     */
    public final @NonNull MarkerOptions markerOptions() {
        return this.markerOptions;
    }

    /**
     * Set the marker options for this marker
     *
     * @param markerOptions new options
     * @return this marker
     */
    public final @NonNull Marker markerOptions(final @NonNull MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
        return this;
    }

    /**
     * Set the marker options for this marker
     *
     * @param markerOptionsBuilder new options
     * @return this marker
     */
    public final @NonNull Marker markerOptions(final MarkerOptions.@NonNull Builder markerOptionsBuilder) {
        this.markerOptions(markerOptionsBuilder.build());
        return this;
    }

    // Begin polyline factory methods

    /**
     * Create a new polyline from a list of points
     *
     * @param points points
     * @return new polyline
     */
    public static @NonNull Polyline polyline(final @NonNull List<Point> points) {
        return polyline(points.toArray(Point[]::new));
    }

    /**
     * Create a new polyline from a list of points
     *
     * @param points points
     * @return new polyline
     */
    public static @NonNull Polyline polyline(final @NonNull Point @NonNull ... points) {
        return new Polyline(new Point[][]{points});
    }

    /**
     * Create a new multi-polyline from a list of lists of points
     *
     * @param points points
     * @return new polyline
     */
    public static @NonNull Polyline multiPolyline(final @NonNull List<List<Point>> points) {
        return multiPolyline(points.stream().map(l -> l.toArray(Point[]::new)).toArray(Point[][]::new));
    }

    /**
     * Create a new multi-polyline from a list of lists of points
     *
     * @param points points
     * @return new polyline
     */
    public static @NonNull Polyline multiPolyline(final @NonNull Point @NonNull [] @NonNull ... points) {
        return new Polyline(points);
    }
    // End polyline factory methods

    // Begin rectangle factory methods

    /**
     * Create a new rectangle marker from two corner points
     *
     * @param point1 first corner
     * @param point2 second corner
     * @return new rectangle
     */
    public static @NonNull Rectangle rectangle(final @NonNull Point point1, final @NonNull Point point2) {
        return new Rectangle(point1, point2);
    }
    // End rectangle factory methods

    // Begin circle factory methods

    /**
     * Create a new circle marker
     *
     * @param center center point
     * @param radius radius
     * @return new circle
     */
    public static @NonNull Circle circle(final @NonNull Point center, final double radius) {
        return new Circle(center, radius);
    }
    // End circle factory methods

    // Begin icon factory methods

    /**
     * Create a new icon marker
     *
     * @param point location for this marker
     * @param tooltipAnchor tooltip anchor, see {@link Icon#tooltipAnchor()}
     * @param anchor icon anchor, see {@link Icon#anchor()}
     * @param image image key, must be registered with the {@link Pl3xMap#iconRegistry() icon registry}.
     * @param sizeX x size
     * @param sizeZ z size
     * @return new icon
     */
    public static @NonNull Icon icon(
            final @NonNull Point point,
            final @NonNull Point tooltipAnchor,
            final @NonNull Point anchor,
            final @NonNull Key image,
            final int sizeX,
            final int sizeZ
    ) {
        return new Icon(point, tooltipAnchor, anchor, image, sizeX, sizeZ);
    }

    /**
     * Create a new icon marker
     *
     * @param point location for this marker
     * @param image image key, must be registered with the {@link Pl3xMap#iconRegistry() icon registry}.
     * @param sizeX x size
     * @param sizeZ z size
     * @return new icon
     */
    public static @NonNull Icon icon(
            final @NonNull Point point,
            final @NonNull Key image,
            final int sizeX,
            final int sizeZ
    ) {
        return icon(point, Point.of(0, 0), Point.of(0, 0), image, sizeX, sizeZ);
    }

    /**
     * Create a new icon marker
     *
     * @param point location for this marker
     * @param image image key, must be registered with the {@link Pl3xMap#iconRegistry() icon registry}.
     * @param size size to use for X and Z size
     * @return new icon
     */
    public static @NonNull Icon icon(
            final @NonNull Point point,
            final @NonNull Key image,
            final int size
    ) {
        return icon(point, Point.of(0, 0), Point.of(0, 0), image, size, size);
    }
    // End icon factory methods

    // Begin multipolygon factory methods
    public static @NonNull MultiPolygon multiPolygon(final @NonNull MultiPolygon.MultiPolygonPart @NonNull ... polygons) {
        return multiPolygon(Arrays.asList(polygons));
    }

    public static @NonNull MultiPolygon multiPolygon(final @NonNull List<MultiPolygon.MultiPolygonPart> polygons) {
        return new MultiPolygon(polygons);
    }
    // End multipolygon factory methods

    // Begin polygon factory methods
    public static @NonNull Polygon polygon(final @NonNull Point @NonNull ... points) {
        return polygon(points, Collections.emptyList());
    }

    public static @NonNull Polygon polygon(final @NonNull List<Point> points) {
        return polygon(points.toArray(Point[]::new));
    }

    public static @NonNull Polygon polygon(final @NonNull Point @NonNull [] mainPolygon, final @NonNull List<Point[]> negativeSpace) {
        return new Polygon(mainPolygon, negativeSpace);
    }

    public static @NonNull Polygon polygon(final @NonNull Point @NonNull [] mainPolygon, final @NonNull Point @NonNull [] @NonNull ... negativeSpace) {
        return polygon(mainPolygon, Arrays.asList(negativeSpace));
    }

    public static @NonNull Polygon polygon(final @NonNull List<Point> mainPolygon, final @NonNull Point @NonNull [] @NonNull ... negativeSpace) {
        return polygon(mainPolygon.toArray(Point[]::new), Arrays.asList(negativeSpace));
    }

    public static @NonNull Polygon polygon(final @NonNull List<Point> mainPolygon, final @NonNull List<List<Point>> negativeSpace) {
        return polygon(mainPolygon, negativeSpace.stream().map(list -> list.toArray(Point[]::new)).toArray(Point[][]::new));
    }
    // End polygon factory methods

}
