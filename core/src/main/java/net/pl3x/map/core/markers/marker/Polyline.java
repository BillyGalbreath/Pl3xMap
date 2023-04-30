/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.markers.marker;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a line of points for a polyline or polygon shape.
 * <p>
 * This line could be used as a polygon's outer poly,
 * or as inner poly to punch holes into the outer poly.
 * <p>
 * A minimum of 2 {@link Point}s are needed to create a valid and
 * visible line for a polyline on the map.
 * <p>
 * A minimum of 3 {@link Point}s are needed to create a valid and
 * visible line for a polygon on the map.
 * <p>
 * The last point you add does not need to be the
 * same as the first point you added for a polygon.
 */
@SuppressWarnings("UnusedReturnValue")
public class Polyline extends Marker<@NonNull Polyline> {
    private final List<@NonNull Point> points = new ArrayList<>();

    private Polyline(@NonNull String key) {
        super("line", key);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key   identifying key
     * @param point point to add
     */
    public Polyline(@NonNull String key, @NonNull Point point) {
        this(key);
        addPoint(point);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key    identifying key
     * @param points points to add
     */
    public Polyline(@NonNull String key, @NonNull Point @NonNull ... points) {
        this(key);
        addPoint(points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key    identifying key
     * @param points points to add
     */
    public Polyline(@NonNull String key, @NonNull Collection<@NonNull Point> points) {
        this(key);
        addPoint(points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key   identifying key
     * @param point point to add
     * @return a new line
     */
    public static @NonNull Polyline of(@NonNull String key, @NonNull Point point) {
        return new Polyline(key, point);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key    identifying key
     * @param points points to add
     * @return a new line
     */
    public static @NonNull Polyline of(@NonNull String key, @NonNull Point @NonNull ... points) {
        return new Polyline(key, points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param key    identifying key
     * @param points points to add
     * @return a new line
     */
    public static @NonNull Polyline of(@NonNull String key, @NonNull Collection<@NonNull Point> points) {
        return new Polyline(key, points);
    }

    /**
     * Get the list of points in this polyline.
     *
     * @return list of points
     */
    public @NonNull List<@NonNull Point> getPoints() {
        return this.points;
    }

    /**
     * Clear the list of points in this polyline.
     *
     * @return this polyline
     */
    public @NonNull Polyline clearPoints() {
        this.points.clear();
        return this;
    }

    /**
     * Add the first point as the end point, closing off the shape.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @return this line
     */
    public @NonNull Polyline loop() {
        Preconditions.checkState(this.points.size() > 0, "No points to loop back on");
        Point first = this.points.get(0);
        Point last = this.points.get(this.points.size() - 1);
        Preconditions.checkState(!first.equals(last), "First and last points are the same");
        this.points.add(first);
        return this;
    }

    /**
     * Add a point to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param point point to add
     * @return this line
     */
    public @NonNull Polyline addPoint(@NonNull Point point) {
        Preconditions.checkNotNull(point, "Polyline point is null");
        this.points.add(point);
        return this;
    }

    /**
     * Add points to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return this line
     */
    public @NonNull Polyline addPoint(@NonNull Point @NonNull ... points) {
        Preconditions.checkNotNull(points, "Polyline points is null");
        for (Point point : points) {
            addPoint(point);
        }
        return this;
    }

    /**
     * Add points to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return this line
     */
    public @NonNull Polyline addPoint(@NonNull Collection<@NonNull Point> points) {
        Preconditions.checkNotNull(points, "Polyline points is null");
        this.points.addAll(points);
        return this;
    }

    /**
     * Remove a point from this polyline.
     *
     * @param point point to remove
     * @return this polyline
     */
    public @NonNull Polyline removePoint(@NonNull Point point) {
        Preconditions.checkNotNull(point, "Polyline point is null");
        this.points.remove(point);
        return this;
    }

    /**
     * Remove points from this polyline.
     *
     * @param points points to remove
     * @return this polyline
     */
    public @NonNull Polyline removePoint(@NonNull Point @NonNull ... points) {
        Preconditions.checkNotNull(points, "Polyline points is null");
        for (Point point : points) {
            removePoint(point);
        }
        return this;
    }

    /**
     * Remove points from this polyline.
     *
     * @param points points to remove
     * @return this polyline
     */
    public @NonNull Polyline removePoint(@NonNull Collection<@NonNull Point> points) {
        Preconditions.checkNotNull(points, "Polyline points is null");
        this.points.removeAll(points);
        return this;
    }

    @Override
    public @NonNull JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("points", getPoints());
        wrapper.addProperty("pane", getPane());
        return wrapper.getJsonObject();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Polyline other = (Polyline) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getPoints(), other.getPoints())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPoints(), getPane(), getOptions());
    }

    @Override
    public @NonNull String toString() {
        return "Line{"
                + "key=" + getKey()
                + ",points=" + getPoints()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
