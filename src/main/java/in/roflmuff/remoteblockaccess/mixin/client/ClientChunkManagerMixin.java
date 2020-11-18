package in.roflmuff.remoteblockaccess.mixin.client;

import in.roflmuff.remoteblockaccess.core.LocalChunkManager;
import in.roflmuff.remoteblockaccess.core.ModItems;
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


interface Dummy {

}

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin{

    @Shadow @Final
    private WorldChunk emptyChunk;

    @Shadow @Nullable public abstract WorldChunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl);
    @Shadow public abstract LightingProvider getLightingProvider();

    @Shadow private volatile ClientChunkManager.ClientChunkMap chunks;
    @Shadow @Final private ClientWorld world;
    private LocalChunkManager localChunkManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientWorld world, int loadDistance, CallbackInfo ci) {
        localChunkManager = new LocalChunkManager(world, (ClientChunkManager) (Object) this);
    }

    @Inject(method = "getChunk", at = @At("RETURN"), cancellable = true)
    private void mixinGetChunk(int x, int z, ChunkStatus chunkStatus, boolean orEmpty, CallbackInfoReturnable<WorldChunk> ci) {
        // Ignore already loaded chunks.
        if (ci.getReturnValue() != (orEmpty ? emptyChunk : null)) {
            return;
        }

        // Try and pull the chunk from our cache.
        WorldChunk chunk = localChunkManager.getChunk(x, z);
        if (chunk != null) {
            ci.setReturnValue(chunk);
        }
    }

    @Inject(method = "loadChunkFromPacket", at = @At("RETURN"))
    private void mixinUnloadFakeChunk(int x, int z, BiomeArray biomes, PacketByteBuf buf, CompoundTag tag, int verticalStripBitmask, boolean complete, CallbackInfoReturnable<WorldChunk> cir) {
        // Store the latest packet we recieved in our cache...
        WorldChunk chunk = cir.getReturnValue();
        if (chunk == null) {
            chunk = new WorldChunk(this.world, new ChunkPos(x, z), biomes);
            chunk.loadFromPacket(biomes, buf, tag, verticalStripBitmask);
        }

        localChunkManager.saveChunk(chunk);
    }
}
