package info.ata4.minecraft.minema.client.modules;

import info.ata4.minecraft.minema.Minema;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkPreloader extends CaptureModule {

    @SubscribeEvent
    public void onTick(RenderTickEvent evt) {
        if (evt.phase != Phase.START) {
            return;
        }

        ChunkRenderDispatcher renderDispatcher = MC.levelRenderer.chunkRenderDispatcher;
        if (renderDispatcher != null) {
            // 250ms timeout
            // TODO: verify no timeout
            renderDispatcher.uploadAllPendingUploads();
            RenderRegionCache renderRegionCache = new RenderRegionCache();
            MC.levelRenderer.renderChunksInFrustum.forEach(info -> {
                if (info.chunk.isDirty()) {
                    renderDispatcher.rebuildChunkSync(info.chunk, renderRegionCache);
                    info.chunk.setNotDirty();
                }
            });
        }
    }

    @Override
    protected void doEnable() throws Exception {
        if (Minema.instance.getConfig().forcePreloadChunks.get()) {
            ChunkRenderDispatcher chunks = MC.levelRenderer.chunkRenderDispatcher;
            ViewArea frustum = MC.levelRenderer.viewArea;
            if (chunks != null && frustum != null) {
                RenderRegionCache renderRegionCache = new RenderRegionCache();
                for (RenderChunk chunk : frustum.chunks) {
                    if (chunk.getCompiledChunk() == CompiledChunk.UNCOMPILED) {
                        chunks.rebuildChunkSync(chunk, renderRegionCache);
                    }
                }
            }
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void doDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    protected boolean checkEnable() {
        return Minema.instance.getConfig().preloadChunks.get();
    }

}