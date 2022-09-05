package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class UpdateMarkerData extends BukkitRunnable {
    private final Gson gson = new GsonBuilder()
            //.setPrettyPrinting()
            //.disableHtmlEscaping()
            .serializeNulls()
            .registerTypeHierarchyAdapter(Marker.class, new Adapter())
            .setLenient()
            .create();

    private final MapWorld mapWorld;
    private final Map<Key, Long> lastUpdated = new HashMap<>();

    public UpdateMarkerData(MapWorld mapWorld) {
        this.mapWorld = mapWorld;
    }

    @Override
    public void run() {
        Map<String, Integer> layers = new HashMap<>();

        this.mapWorld.getLayerRegistry().entries().forEach((key, layer) -> {
            layers.put(key.getKey(), layer.getUpdateInterval());

            long now = System.currentTimeMillis() / 1000;
            long lastUpdate = this.lastUpdated.getOrDefault(key, 0L);

            if (now - lastUpdate > layer.getUpdateInterval()) {
                FileUtil.write(this.gson.toJson(layer.getMarkers()), this.mapWorld.getMarkersDir().resolve(key + ".json"));
                this.lastUpdated.put(key, now);
            }
        });

        FileUtil.write(this.gson.toJson(layers), this.mapWorld.getTilesDir().resolve("markers.json"));
    }

    private static class Adapter implements JsonSerializer<Marker> {
        @Override
        @NotNull
        public JsonElement serialize(@NotNull Marker marker, @NotNull Type type, @NotNull JsonSerializationContext context) {
            JsonArrayWrapper wrapper = new JsonArrayWrapper();
            wrapper.add(marker.getType());
            wrapper.add(marker);
            if (marker.getOptions() != null) {
                wrapper.add(marker.getOptions());
            }
            return wrapper.getJsonArray();
        }
    }
}
