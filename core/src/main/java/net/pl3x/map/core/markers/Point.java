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
package net.pl3x.map.core.markers;

import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;

public record Point(int x, int z) implements JsonSerializable {
    public static final Point ZERO = new Point(0, 0);

    public static @NonNull Point of(int x, int z) {
        return new Point(x, z);
    }

    public static @NonNull Point of(double x, double z) {
        return of((int) Math.floor(x), (int) Math.floor(z));
    }

    @Override
    public @NonNull JsonObject toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("x", x());
        wrapper.addProperty("z", z());
        return wrapper.getJsonObject();
    }

    public static @NonNull Point fromJson(@NonNull JsonObject obj) {
        return Point.of(obj.get("x").getAsInt(), obj.get("z").getAsInt());
    }
}
