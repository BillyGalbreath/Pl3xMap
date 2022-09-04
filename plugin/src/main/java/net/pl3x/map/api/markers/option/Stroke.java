package net.pl3x.map.api.markers.option;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stroke properties of a marker.
 */
public class Stroke extends Option {
    private Boolean enabled;
    private Integer weight;
    private Integer color;
    private LineCapShape lineCapShape;
    private LineJoinShape lineJoinShape;
    private String dashPattern;
    private String dashOffset;

    /**
     * Create a stroke rule with default options.
     */
    public Stroke() {
    }

    /**
     * Create a stroke rule.
     *
     * @param weight stroke weight
     * @param color  argb color
     */
    public Stroke(int weight, int color) {
        setWeight(weight);
        setColor(color);
    }

    /**
     * Whether to draw stroke along the path.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true if stroke is enabled
     */
    @Nullable
    public Boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set whether to draw stroke along the path.
     * <p>
     * Setting to false will disable borders on polygons or circles.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param enabled whether stroke is enabled
     * @return this stroke rule
     */
    @NotNull
    public Stroke setEnabled(@Nullable Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Get the weight of this stroke rule.
     * <p>
     * Defaults to '<code>3</code>' if null.
     *
     * @return stroke weight
     */
    @Nullable
    public Integer getWeight() {
        return this.weight;
    }

    /**
     * Set the weight for this stroke rule.
     * <p>
     * Defaults to '<code>3</code>' if null.
     *
     * @param weight new stroke weight
     * @return this stroke rule
     */
    @NotNull
    public Stroke setWeight(@Nullable Integer weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Get the color of this stroke rule.
     * <p>
     * Defaults to '<code>#FF3388FF</code>' if null.
     *
     * @return argb color
     */
    @Nullable
    public Integer getColor() {
        return this.color;
    }

    /**
     * Set the color of this stroke rule.
     * <p>
     * Defaults to '<code>#FF3388FF</code>' if null.
     *
     * @param color argb color
     * @return this stroke rule
     */
    @NotNull
    public Stroke setColor(@Nullable Integer color) {
        this.color = color;
        return this;
    }

    /**
     * Get the shape to be used at the end of the stroke.
     * <p>
     * Defaults to '<code>{@link LineCapShape#ROUND}</code>' if null.
     *
     * @return line cap shape
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linecap">MDN stroke-linecap</a>
     */
    @Nullable
    public LineCapShape getLineCapShape() {
        return this.lineCapShape;
    }

    /**
     * Set the shape to be used at the end of the stroke.
     * <p>
     * Defaults to '<code>{@link LineCapShape#ROUND}</code>' if null.
     *
     * @param lineCapShape line cap shape
     * @return this stroke rule
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linecap">MDN stroke-linecap</a>
     */
    @NotNull
    public Stroke setLineCapShape(@Nullable LineCapShape lineCapShape) {
        this.lineCapShape = lineCapShape;
        return this;
    }

    /**
     * Get the shape to be used at the corners of the stroke.
     * <p>
     * Defaults to '<code>{@link LineJoinShape#ROUND}</code>' if null.
     *
     * @return line join shape
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linejoin">MDN stroke-linejoin</a>
     */
    @Nullable
    public LineJoinShape getLineJoinShape() {
        return this.lineJoinShape;
    }

    /**
     * Set the shape to be used at the corners of the stroke.
     * <p>
     * Defaults to '<code>{@link LineJoinShape#ROUND}</code>' if null.
     *
     * @param lineJoinShape line join shape
     * @return this stroke rule
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linejoin">MDN stroke-linejoin</a>
     */
    @NotNull
    public Stroke setLineJoinShape(@Nullable LineJoinShape lineJoinShape) {
        this.lineJoinShape = lineJoinShape;
        return this;
    }

    /**
     * Get the stroke dash pattern.
     * <p>
     * Note: Doesn't work in some old browsers.
     *
     * @return stroke dash pattern
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dasharray">MDN stroke-dasharray</a>
     */
    @Nullable
    public String getDashPattern() {
        return this.dashPattern;
    }

    /**
     * Set the stroke dash pattern.
     * <p>
     * Note: Doesn't work in some old browsers.
     *
     * @param dashPattern dash pattern
     * @return this stroke rule
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dasharray">MDN stroke-dasharray</a>
     */
    @NotNull
    public Stroke setDashPattern(@Nullable String dashPattern) {
        this.dashPattern = dashPattern;
        return this;
    }

    /**
     * Get the distance into the dash pattern to start the dash.
     * <p>
     * Note: Doesn't work in some old browsers.
     *
     * @return dash offset
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dashoffset">MDN stroke-dashoffset</a>
     * @see <a href="https://developer.mozilla.org/docs/Web/API/CanvasRenderingContext2D/setLineDash#Browser_compatibility">Browser compatibility</a>
     */
    @Nullable
    public String getDashOffset() {
        return this.dashOffset;
    }

    /**
     * Set the distance into the dash pattern to start the dash.
     * <p>
     * Note: Doesn't work in some old browsers.
     *
     * @param dashOffset dash offset
     * @return this stroke rule
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dashoffset">MDN stroke-dashoffset</a>
     * @see <a href="https://developer.mozilla.org/docs/Web/API/CanvasRenderingContext2D/setLineDash#Browser_compatibility">Browser compatibility</a>
     */
    @NotNull
    public Stroke setDashOffset(@Nullable String dashOffset) {
        this.dashOffset = dashOffset;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(isEnabled());
        wrapper.add(getWeight());
        wrapper.add(getColor());
        wrapper.add(getLineCapShape());
        wrapper.add(getLineJoinShape());
        wrapper.add(getDashPattern());
        wrapper.add(getDashOffset());
        return wrapper.getJsonArray();
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
        Stroke other = (Stroke) o;
        return Objects.equals(isEnabled(), other.isEnabled())
                && Objects.equals(getWeight(), other.getWeight())
                && Objects.equals(getColor(), other.getColor())
                && Objects.equals(getLineCapShape(), other.getLineCapShape())
                && Objects.equals(getLineJoinShape(), other.getLineJoinShape())
                && Objects.equals(getDashPattern(), other.getDashPattern())
                && Objects.equals(getDashOffset(), other.getDashOffset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isEnabled(), getWeight(), getColor(), getLineCapShape(), getLineJoinShape(), getDashPattern(), getDashOffset());
    }

    @Override
    public String toString() {
        return "Stroke{"
                + "enabled=" + isEnabled()
                + ",weight=" + getWeight()
                + ",color=" + getColor()
                + ",lineCapShape=" + getLineCapShape()
                + ",lineJoinShape=" + getLineJoinShape()
                + ",dashPattern=" + getDashPattern()
                + ",dashOffset=" + getDashOffset()
                + "}";
    }

    /**
     * Represents the shape to be used at the corners of the stroke.
     *
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linecap">MDN stroke-linecap</a>
     */
    public enum LineCapShape {
        /**
         * The butt value indicates that the stroke for each subpath does
         * not extend beyond its two endpoints. On a zero length subpath,
         * the path will not be rendered at all.
         */
        BUTT,
        /**
         * The round value indicates that at the end of each subpath the
         * stroke will be extended by a half circle with a diameter equal
         * to the stroke width. On a zero length subpath, the stroke
         * consists of a full circle centered at the subpath's point.
         */
        ROUND,
        /**
         * The square value indicates that at the end of each subpath the
         * stroke will be extended by a rectangle with a width equal to
         * half the width of the stroke and a height equal to the width
         * of the stroke. On a zero length subpath, the stroke consists
         * of a square with its width equal to the stroke width, centered
         * at the subpath's point.
         */
        SQUARE
    }

    /**
     * Represents the shape to be used at the corners of the stroke.
     *
     * @see <a href="https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linejoin">MDN stroke-linejoin</a>
     */
    public enum LineJoinShape {
        /**
         * The miter value indicates that a sharp corner is to be used
         * to join path segments. The corner is formed by extending the
         * outer edges of the stroke at the tangents of the path
         * segments until they intersect.
         */
        MITER,
        /**
         * The round value indicates that a round corner is to be used
         * to join path segments.
         */
        ROUND,
        /**
         * The bevel value indicates that a bevelled corner is to be
         * used to join path segments.
         */
        BEVEL
    }
}
