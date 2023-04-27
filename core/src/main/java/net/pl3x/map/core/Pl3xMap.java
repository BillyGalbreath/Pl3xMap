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
package net.pl3x.map.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.platform.AudienceProvider;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.httpd.HttpdServer;
import net.pl3x.map.core.image.io.IO;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.metrics.Metrics;
import net.pl3x.map.core.player.PlayerRegistry;
import net.pl3x.map.core.registry.BlockRegistry;
import net.pl3x.map.core.registry.IconRegistry;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.registry.WorldRegistry;
import net.pl3x.map.core.renderer.heightmap.HeightmapRegistry;
import net.pl3x.map.core.renderer.task.RegionProcessor;
import net.pl3x.map.core.renderer.task.UpdateSettingsData;
import net.pl3x.map.core.scheduler.Scheduler;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.SpiFix;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Block;
import net.pl3x.map.core.world.Blocks;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Pl3xMap {
    public static @NonNull Pl3xMap api() {
        return Provider.api();
    }

    private final HttpdServer httpdServer;
    private final RegionProcessor regionProcessor;
    private final Scheduler scheduler;

    private final BlockRegistry blockRegistry;
    private final HeightmapRegistry heightmapRegistry;
    private final IconRegistry iconRegistry;
    private final PlayerRegistry playerRegistry;
    private final RendererRegistry rendererRegistry;
    private final WorldRegistry worldRegistry;

    private ExecutorService renderExecutor;

    private Metrics metrics;
    private boolean enabled;

    public Pl3xMap() {
        try {
            // Due to these bugs(?) in spi
            // * relocated libraries cant find their services (xnio fails)
            // * imageio fails to find twelvemonkeys spis at all
            // I am forced to load them all myself instead of relying on the META-INF
            SpiFix.forceRegisterSpis();
        } catch (Throwable ignore) {
        }

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // setup internal server
        this.httpdServer = new HttpdServer();

        // setup tasks
        this.regionProcessor = new RegionProcessor();
        this.scheduler = new Scheduler();

        // setup registries
        this.blockRegistry = new BlockRegistry();
        this.heightmapRegistry = new HeightmapRegistry();
        this.iconRegistry = new IconRegistry();
        this.playerRegistry = new PlayerRegistry();
        this.rendererRegistry = new RendererRegistry();
        this.worldRegistry = new WorldRegistry();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public @NonNull HttpdServer getHttpdServer() {
        return this.httpdServer;
    }

    public @NonNull RegionProcessor getRegionProcessor() {
        return this.regionProcessor;
    }

    public @NonNull BlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    public @NonNull HeightmapRegistry getHeightmapRegistry() {
        return this.heightmapRegistry;
    }

    public @NonNull IconRegistry getIconRegistry() {
        return this.iconRegistry;
    }

    public @NonNull PlayerRegistry getPlayerRegistry() {
        return this.playerRegistry;
    }

    public @NonNull RendererRegistry getRendererRegistry() {
        return this.rendererRegistry;
    }

    public @NonNull WorldRegistry getWorldRegistry() {
        return this.worldRegistry;
    }

    public @NonNull ExecutorService getRenderExecutor() {
        return this.renderExecutor;
    }

    public @NonNull Scheduler getScheduler() {
        return this.scheduler;
    }

    public void enable() {
        // load up configs
        Logger.debug("Loading configs");
        Config.reload();
        Lang.reload();
        ColorsConfig.reload();

        // initialize icons
        getIconRegistry().init();

        // load blocks _after_ we loaded colors
        Logger.debug("Registering blocks");
        Blocks.registerDefaults();
        loadBlocks();

        // create the executor service
        Logger.debug("Creating services");
        this.renderExecutor = ThreadFactory.createService("Pl3xMap-Renderer", Config.RENDER_THREADS);

        // register built in tile image types
        Logger.debug("Registering tile image types");
        IO.register();

        // register built-in heightmaps
        Logger.debug("Registering heightmaps");
        getHeightmapRegistry().register();

        // register built-in renderers
        Logger.debug("Registering renderers");
        getRendererRegistry().register();

        // load up already loaded worlds
        Logger.debug("Registering worlds");
        loadWorlds();

        // load up players already connected to the server
        Logger.debug("Registering players");
        loadPlayers();

        // start integrated server
        Logger.debug("Starting internal server");
        getHttpdServer().startServer();

        // start tasks
        Logger.debug("Starting region processor");
        getRegionProcessor().start(10000L);

        Logger.debug("Starting update settings data task");
        getScheduler().addTask(new UpdateSettingsData());

        Logger.info("Platform: " + getPlatform());
        Logger.info("Version: " + getVersion());

        try {
            this.metrics = new Metrics(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.enabled = true;
    }

    public void disable() {
        if (this.metrics != null) {
            this.metrics.shutdown();
            this.metrics = null;
        }

        this.enabled = false;

        // stop tasks
        Logger.debug("Stopping tasks");
        getScheduler().cancelAll();
        getRegionProcessor().stop();
        if (this.renderExecutor != null) {
            this.renderExecutor.shutdownNow();
        }

        // stop integrated server
        Logger.debug("Stopping internal server");
        getHttpdServer().stopServer();

        // unload all players
        Logger.debug("Unregistering players");
        getPlayerRegistry().unregister();

        // unload all map worlds
        Logger.debug("Unregistering worlds");
        getWorldRegistry().unregister();

        // unregister renderers
        Logger.debug("Unregistering renderers");
        getRendererRegistry().unregister();

        // unregister icons
        Logger.debug("Unregistering icons");
        getIconRegistry().unregister();

        // unregister heightmaps
        Logger.debug("Unregistering heightmaps");
        getHeightmapRegistry().unregister();

        // unregister tile image types
        Logger.debug("Unregistering tile image types");
        IO.unregister();

        // unregister blocks
        getBlockRegistry().unregister();
    }

    public abstract @NonNull String getPlatform();

    public abstract @NonNull String getVersion();

    public abstract int getMaxPlayers();

    public abstract boolean getOnlineMode();

    public abstract int getOperatorUserPermissionLevel();

    public abstract @NonNull AudienceProvider adventure();

    public abstract @NonNull Path getMainDir();

    public abstract @NonNull Path getJarPath();

    public abstract int getColorForPower(byte power);

    public abstract @Nullable Block getFlower(@NonNull World world, @NonNull Biome biome, int blockX, int blockY, int blockZ);

    protected abstract void loadBlocks();

    protected abstract void loadWorlds();

    protected abstract void loadPlayers();

    public abstract @NonNull World cloneWorld(@NonNull World world);

    protected static final class Provider {
        static Pl3xMap api;

        static @NonNull Pl3xMap api() {
            return Provider.api;
        }
    }

    public static final class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private final String name;
        private final int threads;

        private final AtomicInteger id = new AtomicInteger();

        public ThreadFactory(@NonNull String name, int threads) {
            this.name = name;
            this.threads = threads;
        }

        public static @NonNull ExecutorService createService(@NonNull String name) {
            return createService(new ThreadFactory(name, 1));
        }

        public static @NonNull ExecutorService createService(@NonNull String name, int threads) {
            int max = Runtime.getRuntime().availableProcessors() / 2;
            int parallelism = Mathf.clamp(1, max, threads < 1 ? max : threads);
            return createService(new ThreadFactory(name, parallelism));
        }

        private static @NonNull ExecutorService createService(@NonNull ThreadFactory factory) {
            return new ForkJoinPool(factory.threads, factory, null, false);
        }

        @Override
        public @NonNull ForkJoinWorkerThread newThread(@NonNull ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            // use current classloader, this fixes ClassLoading issues with forge
            thread.setContextClassLoader(Pl3xMap.class.getClassLoader());
            thread.setName(this.threads > 1 ? String.format("%s-%d", this.name, this.id.getAndIncrement()) : this.name);
            return thread;
        }
    }
}
