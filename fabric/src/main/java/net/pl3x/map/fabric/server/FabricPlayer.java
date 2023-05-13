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
package net.pl3x.map.fabric.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.world.World;
import net.pl3x.map.fabric.server.duck.AccessServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FabricPlayer extends Player {
    public FabricPlayer(@NonNull ServerPlayer player) {
        super(player.getScoreboardName(), player);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ServerPlayer getPlayer() {
        return super.getPlayer();
    }

    @Override
    public @NonNull String getName() {
        return getPlayer().getScoreboardName();
    }

    @Override
    public @NonNull UUID getUUID() {
        return getPlayer().getUUID();
    }

    @Override
    public @NonNull World getWorld() {
        ServerLevel level = getPlayer().getLevel();
        String name = level.dimension().location().toString();
        return Pl3xMap.api().getWorldRegistry().getOrDefault(name, () -> new FabricWorld(level, name));
    }

    @Override
    public @NonNull Point getPosition() {
        Vec3 loc = getPlayer().position();
        return Point.of(loc.x(), loc.z());
    }

    @Override
    public float getYaw() {
        return getPlayer().getYRot();
    }

    @Override
    public int getHealth() {
        return Math.round(getPlayer().getHealth());
    }

    @Override
    public int getArmorPoints() {
        AttributeInstance attr = getPlayer().getAttribute(Attributes.ARMOR);
        return attr == null ? 0 : (int) Math.round(attr.getValue());
    }

    @Override
    public @Nullable URL getSkin() {
        try {
            Property property = getPlayer().getGameProfile().getProperties().get("textures").stream().findFirst().orElse(null);
            if (property == null) {
                return null;
            }
            String json = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(json);
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("textures");
            if (jsonObject == null) {
                return null;
            }
            JsonObject skin = jsonObject.get("SKIN").getAsJsonObject();
            if (skin == null) {
                return null;
            }
            String url = skin.get("url").getAsString();
            if (url == null) {
                return null;
            }
            return new URI(url).toURL();
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public boolean isInvisible() {
        return getPlayer().isInvisible();
    }

    @Override
    public boolean isNPC() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return getPlayer().isSpectator();
    }

    @Override
    public boolean isPersistentlyHidden() {
        return ((AccessServerPlayer) getPlayer()).isHidden();
    }

    @Override
    public void setPersistentlyHidden(boolean hidden) {
        ((AccessServerPlayer) getPlayer()).setHidden(hidden);
    }

    @Override
    public @NonNull String toString() {
        return "FabricPlayer{"
                + "player=" + getPlayer().getUUID()
                + "}";
    }
}
