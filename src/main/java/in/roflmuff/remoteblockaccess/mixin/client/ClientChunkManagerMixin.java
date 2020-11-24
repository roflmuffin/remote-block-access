package in.roflmuff.remoteblockaccess.mixin.client;

import in.roflmuff.remoteblockaccess.core.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;


@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin{

    @Shadow @Final
    private WorldChunk emptyChunk;

    @Shadow @Nullable public abstract WorldChunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl);
    @Shadow public abstract LightingProvider getLightingProvider();

    @Shadow private volatile ClientChunkManager.ClientChunkMap chunks;
    @Shadow @Final private ClientWorld world;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientWorld world, int loadDistance, CallbackInfo ci) {
        if (GlobalClientChunkManager.Instance == null) {
            GlobalClientChunkManager.Instance = new GlobalClientChunkManager();
        }

        System.out.println("Initializing chunk manager for dimension: " + world.getRegistryKey().getValue());
        GlobalClientChunkManager.Instance.initializeForWorld(world.getRegistryKey().getValue());
    }

    @Inject(method = "getChunk", at = @At("RETURN"), cancellable = true)
    private void mixinGetChunk(int x, int z, ChunkStatus chunkStatus, boolean orEmpty, CallbackInfoReturnable<WorldChunk> ci) {
        // Ignore already loaded chunks.
        if (ci.getReturnValue() != (orEmpty ? emptyChunk : null)) {
            return;
        }
/*
        ChunkQueueItem queueItem = ClientChunkQueue.Instance.peek();
        if (queueItem != null && queueItem.chunkPos == new ChunkPos(x, z).toLong()) {
            System.out.println("We should be trying to load our item from cache!");
            WorldChunk chunk = GlobalClientChunkManager.Instance.getLocalManager(queueItem.dimension.getValue()).getChunk(x, z);
            ci.setReturnValue(chunk);
            //ClientChunkQueue.Instance.pop();
        }*/
    }

    @Inject(method = "loadChunkFromPacket", at = @At("RETURN"))
    private void mixinUnloadFakeChunk(int x, int z, BiomeArray biomes, PacketByteBuf buf, CompoundTag tag, int verticalStripBitmask, boolean complete, CallbackInfoReturnable<WorldChunk> cir) {
        // Store the latest packet we recieved in our cache...
        WorldChunk chunk = cir.getReturnValue();

/*
        ChunkQueueItem queueItem = ClientChunkQueue.Instance.peek();
        if (queueItem != null && queueItem.chunkPos == new ChunkPos(x, z).toLong()) {
            System.out.println("We should be trying to save our item into the cache.");
            WorldChunk newChunk = new WorldChunk(world, new ChunkPos(x,z), biomes);
            newChunk.loadFromPacket(biomes, buf, tag, verticalStripBitmask);
            GlobalClientChunkManager.Instance.getLocalManager(queueItem.dimension.getValue()).saveChunk(newChunk);

            //chunk = new WorldChunk(this.world, new ChunkPos(x, z), biomes);
            //chunk.loadFromPacket(biomes, buf, tag, verticalStripBitmask);
        }
*/

        //GlobalClientChunkManager.Instance.getLocalManager(world).saveChunk(chunk);
    }
}
