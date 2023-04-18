package net.pl3x.map.core.world;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.registry.BiomeRegistry;
import net.pl3x.map.core.renderer.Renderer;
import net.pl3x.map.core.renderer.task.RegionFileWatcher;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.util.Mathf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class World {
    private final String name;
    private final Path regionDirectory;
    private final Path tilesDirectory;
    private final WorldConfig worldConfig;

    private final long seed;
    private final Point spawn;

    private final BiomeManager biomeManager;
    private final BiomeRegistry biomeRegistry;

    private final LoadingCache<Long, Region> regionCache;
    private final RegionModifiedState regionModifiedState;
    private final RegionFileWatcher regionFileWatcher;
    private final Map<String, Renderer.Builder> renderers = new LinkedHashMap<>();

    private boolean paused = false;

    public World(String name, long seed, Point spawn, Path regionDirectory, WorldConfig worldConfig) {
        this.name = name;
        this.seed = seed;
        this.spawn = spawn;

        this.regionDirectory = regionDirectory;
        this.tilesDirectory = FileUtil.getWebDir().resolve("tiles").resolve(name.replace(":", "-"));

        this.worldConfig = worldConfig;

        this.biomeManager = new BiomeManager(this);
        this.biomeRegistry = new BiomeRegistry();

        this.regionCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(this::loadRegion);

        this.regionModifiedState = new RegionModifiedState(this);
        this.regionFileWatcher = new RegionFileWatcher(this);
        this.regionFileWatcher.start();

        worldConfig.RENDER_RENDERERS.forEach((id, icon) -> {
            Renderer.Builder renderer = Pl3xMap.api().getRendererRegistry().get(id);
            if (renderer == null) {
                return;
            }
            Path path = FileUtil.getWebDir().resolve("images/icon/" + icon + ".png");
            try {
                IconImage image = new IconImage(icon, ImageIO.read(path.toFile()), "png");
                Pl3xMap.api().getIconRegistry().register(image);
            } catch (IOException e) {
                Logger.severe("Cannot load world renderer icon " + path);
                e.printStackTrace();
            }
            this.renderers.put(renderer.key(), renderer);
        });

        Pl3xMap.api().getRegionProcessor().addRegions(this, listRegions());
    }

    public void cleanup() {
        this.regionCache.invalidateAll();
    }

    @NonNull
    public Path getRegionDirectory() {
        return this.regionDirectory;
    }

    @NonNull
    public Path getTilesDirectory() {
        return this.tilesDirectory;
    }

    @NonNull
    public WorldConfig getConfig() {
        return this.worldConfig;
    }

    @NonNull
    public RegionModifiedState getRegionModifiedState() {
        return this.regionModifiedState;
    }

    @NonNull
    public RegionFileWatcher getRegionFileWatcher() {
        return this.regionFileWatcher;
    }

    @NonNull
    public Map<String, Renderer.Builder> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public long getSeed() {
        return this.seed;
    }

    @NonNull
    public Point getSpawn() {
        return this.spawn;
    }

    public int getSkylight() {
        return getConfig().RENDER_SKYLIGHT;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @NonNull
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    @NonNull
    public BiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    public abstract long hashSeed(long seed);

    public abstract boolean hasCeiling();

    public abstract int getMinBuildHeight();

    @NonNull
    public Chunk getChunk(@Nullable Region region, int chunkX, int chunkZ) {
        return getRegion(region, chunkX >> 5, chunkZ >> 5).getChunk(chunkX, chunkZ);
    }

    @NonNull
    public Region getRegion(@Nullable Region region, int regionX, int regionZ) {
        if (region != null && region.getX() == regionX && region.getZ() == regionZ) {
            return region;
        }
        return getRegion(Mathf.asLong(regionX, regionZ));
    }

    @NonNull
    private Region getRegion(@NonNull Long pos) {
        return this.regionCache.get(pos);
    }

    public void unloadRegion(int regionX, int regionZ) {
        unloadRegion(Mathf.asLong(regionX, regionZ));
    }

    private void unloadRegion(Long pos) {
        this.regionCache.invalidate(pos);
    }

    @NonNull
    public Collection<Path> getRegionFiles() {
        try (Stream<Path> stream = Files.list(getRegionDirectory())) {
            return stream.filter(FileUtil.MCA_MATCHER::matches).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list region files in directory '" + getRegionDirectory().toAbsolutePath() + "'", e);
        }
    }

    @NonNull
    public Collection<Point> listRegions() {
        return FileUtil.regionPathsToPoints(this, getRegionFiles());
    }

    @NonNull
    private Region loadRegion(long pos) {
        int x = Mathf.longToX(pos);
        int z = Mathf.longToZ(pos);
        return new Region(this, x, z, getMCAFile(x, z));
    }

    @NonNull
    private Path getMCAFile(int regionX, int regionZ) {
        return getRegionDirectory().resolve("r." + regionX + "." + regionZ + ".mca");
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
        World other = (World) o;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "World{"
                + "name=" + getName()
                + ",seed=" + getSeed()
                + ",spawn=" + getSpawn()
                + "}";
    }
}