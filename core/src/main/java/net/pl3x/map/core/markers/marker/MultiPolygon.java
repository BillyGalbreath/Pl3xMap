/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
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
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a multi-polygon marker.
 */
@SuppressWarnings("UnusedReturnValue")
public class MultiPolygon extends Marker<@NonNull MultiPolygon> {
    private final List<@NonNull Polygon> polygons = new ArrayList<>();

    private MultiPolygon(@NonNull String key) {
        super("multipoly", key);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key     identifying key
     * @param polygon polygon to add
     */
    public MultiPolygon(@NonNull String key, @NonNull Polygon polygon) {
        this(key);
        addPolygon(polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     */
    public MultiPolygon(@NonNull String key, @NonNull Polygon @NonNull ... polygons) {
        this(key);
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     */
    public MultiPolygon(@NonNull String key, @NonNull Collection<@NonNull Polygon> polygons) {
        this(key);
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key     identifying key
     * @param polygon polygon to add
     * @return a new multi-polygon
     */
    public static @NonNull MultiPolygon of(@NonNull String key, @NonNull Polygon polygon) {
        return new MultiPolygon(key, polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static @NonNull MultiPolygon of(@NonNull String key, @NonNull Polygon @NonNull ... polygons) {
        return new MultiPolygon(key, polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static @NonNull MultiPolygon of(@NonNull String key, @NonNull Collection<@NonNull Polygon> polygons) {
        return new MultiPolygon(key, polygons);
    }

    /**
     * Get the list of polygons in this multi-polygon.
     *
     * @return list of polygons
     */
    public @NonNull List<@NonNull Polygon> getPolygons() {
        return this.polygons;
    }

    /**
     * Clear the list of polygons in this multi-polygon.
     *
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon clearPolygons() {
        this.polygons.clear();
        return this;
    }

    /**
     * Add a polygon to this multi-polygon.
     *
     * @param polygon polygon to add
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon addPolygon(@NonNull Polygon polygon) {
        Preconditions.checkNotNull(polygon, "MultiPolygon polygon is null");
        this.polygons.add(polygon);
        return this;
    }

    /**
     * Add polygons to this multi-polygon.
     *
     * @param polygons polygons to add
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon addPolygon(@NonNull Polygon @NonNull ... polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        for (Polygon polygon : polygons) {
            addPolygon(polygon);
        }
        return this;
    }

    /**
     * Add polygons to this multi-polygon.
     *
     * @param polygons polygons to add
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon addPolygon(@NonNull Collection<@NonNull Polygon> polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        this.polygons.addAll(polygons);
        return this;
    }

    /**
     * Remove a polygon from this multi-polygon.
     *
     * @param polygon polygon to remove
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon removePoly(@NonNull Polygon polygon) {
        Preconditions.checkNotNull(polygon, "MultiPolygon polygon is null");
        this.polygons.remove(polygon);
        return this;
    }

    /**
     * Remove polygons from this multi-polygon.
     *
     * @param polygons polygons to remove
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon removePoly(@NonNull Polygon @NonNull ... polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        for (Polygon polygon : polygons) {
            removePoly(polygon);
        }
        return this;
    }

    /**
     * Remove polygons from this multi-polygon.
     *
     * @param polygons polygons to remove
     * @return this multi-polygon
     */
    public @NonNull MultiPolygon removePoly(@NonNull Collection<@NonNull Polygon> polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        this.polygons.removeAll(polygons);
        return this;
    }

    @Override
    public @NonNull JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("polygons", getPolygons());
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
        MultiPolygon other = (MultiPolygon) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getPolygons(), other.getPolygons())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPolygons(), getPane(), getOptions());
    }

    @Override
    public @NonNull String toString() {
        return "MultiPolygon{"
                + "key=" + getKey()
                + ",polygons=" + getPolygons()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
