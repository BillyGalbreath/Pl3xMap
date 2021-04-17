package net.pl3x.map.plugin.configuration;

import net.pl3x.map.plugin.data.ChunkCoordinate;
import net.pl3x.map.plugin.data.Region;
import net.pl3x.map.plugin.util.Numbers;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Decides what blocks, chunks and regions are visible on the map. Even if a
 * chunk exists outside of this limit, it is not displayed.
 *
 */
public final class VisibilityLimit {

    public int centerX = 0;
    public int centerZ = 0;
    public boolean enabled = false;
    private int radius = 1000;
    private int radiusSquared = radius * radius;

    private static final int REGION_SIZE_BLOCKS = Numbers.regionToBlock(1);
    private static final int REGION_SIZE_CHUNKS = Numbers.regionToChunk(1);
    private static final int CHUNK_SIZE_BLOCKS = Numbers.chunkToBlock(1);

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;

        if (this.radius < 0) {
            this.enabled = false;
        }
    }

    public boolean shouldRenderChunk(@NonNull ChunkCoordinate chunk) {
        return shouldRenderChunk(chunk.getX(), chunk.getZ());
    }

    public boolean shouldRenderChunk(int chunkX, int chunkZ) {

        if (!enabled) {
            return true;
        }
        if (radius == 0) {
            return false;
        }

        long blockX = Numbers.chunkToBlock(chunkX);
        long blockZ = Numbers.chunkToBlock(chunkZ);

        // make sure we look at the corner of the chunk that is the closest to the center of our visibility limit
        if (blockX < centerX) {
            blockX += Math.min(CHUNK_SIZE_BLOCKS - 1, centerX - blockX);
        }
        if (blockZ < centerZ) {
            blockZ += Math.min(CHUNK_SIZE_BLOCKS - 1, centerZ - blockZ);
        }

        long distanceSquared = (blockX - centerX) * (blockX - centerX) + (blockZ - centerZ) * (blockZ - centerZ);
        return distanceSquared <= radiusSquared;

    }

    public boolean shouldRenderColumn(int blockX, int blockZ) {
        if (!enabled) {
            return true;
        }
        if (radius == 0) {
            return false;
        }

        long distanceSquared = (blockX - centerX) * (blockX - centerX) + (blockZ - centerZ) * (blockZ - centerZ);
        return distanceSquared <= radiusSquared;
    }

    /**
     * Checks if the specified region should be rendered according to the settings
     * of this world.
     *
     * @param region The region.
     * @return True if it should be rendered, false otherwise.
     */
    public boolean shouldRenderRegion(@NonNull Region region) {
        if (!enabled) {
            return true;
        }
        if (radius == 0) {
            return false;
        }

        long blockX = region.getBlockX();
        long blockZ = region.getBlockZ();

        // make sure we look at the corner of the region that is the closest to the center of our visibility limit
        if (blockX < centerX) {
            blockX += Math.min(REGION_SIZE_BLOCKS - 1, centerX - blockX);
        }
        if (blockZ < centerZ) {
            blockZ += Math.min(REGION_SIZE_BLOCKS - 1, centerZ - blockZ);
        }

        long distanceSquared = (blockX - centerX) * (blockX - centerX) + (blockZ - centerZ) * (blockZ - centerZ);
        return distanceSquared <= radiusSquared;
    }

    /**
     * Counts the amount of chunks in the region for which {@link #shouldRenderChunk(int, int)} returns {@code true}.
     * @param region The region.
     * @return The amount of chunks, from 0 to {@value #REGION_SIZE_CHUNKS} * {@value #REGION_SIZE_CHUNKS}.
     */
    public int countChunksInRegion(Region region) {
        int chunkXStart = region.getChunkX();
        int chunkZStart = region.getChunkZ();
        if (shouldRenderChunk(chunkXStart, chunkZStart)
                && shouldRenderChunk(chunkXStart + REGION_SIZE_CHUNKS - 1, chunkZStart)
                && shouldRenderChunk(chunkXStart, chunkZStart + REGION_SIZE_CHUNKS - 1)
                && shouldRenderChunk(chunkXStart + REGION_SIZE_CHUNKS - 1, chunkZStart + REGION_SIZE_CHUNKS - 1)) {
            // we need to render all four corners, so that means we need to render the
            // entire region
            // (note: this only works because the visibility limit is one single circle)
            return REGION_SIZE_CHUNKS * REGION_SIZE_CHUNKS;
        }

        // check each chunk individually
        int count = 0;
        for (int i = 0; i < REGION_SIZE_CHUNKS; i++) {
            for (int j = 0; j < REGION_SIZE_CHUNKS; j++) {
                if (this.shouldRenderChunk(chunkXStart + i, chunkZStart + j)) {
                    count++;
                }
            }
        }
        return count;
    }

}
