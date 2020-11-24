package in.roflmuff.remoteblockaccess.mixin.client;

import in.roflmuff.remoteblockaccess.core.ChunkQueueItem;
import in.roflmuff.remoteblockaccess.core.ClientChunkQueue;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Inject(method = "getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;",
            at = @At("RETURN"),
            cancellable = true)
    public void getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType, CallbackInfoReturnable<BlockEntity> callbackInfo) {
        ChunkQueueItem queueItem = ClientChunkQueue.Instance.peek();
        if (queueItem != null && pos == queueItem.blockPos) {
            System.out.println("We have tried to access a block ent from another dimension");

        }
    }
}
