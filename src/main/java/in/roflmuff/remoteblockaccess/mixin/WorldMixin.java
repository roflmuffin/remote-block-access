package in.roflmuff.remoteblockaccess.mixin;

import in.roflmuff.remoteblockaccess.core.ChunkQueueItem;
import in.roflmuff.remoteblockaccess.core.ClientChunkQueue;
import in.roflmuff.remoteblockaccess.core.ProxyClientWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow @Final public boolean isClient;

    @Shadow @Final public List<BlockEntity> blockEntities;

    @Inject(method="getBlockEntity",
            at = @At("RETURN"),
            cancellable = true
    )
    public void getBlockEntityMixin(BlockPos pos, CallbackInfoReturnable<BlockEntity> callbackInfo) {
        if (this.isClient) {
            ChunkQueueItem queueItem = ClientChunkQueue.Instance.peek();
            if (queueItem != null && pos.asLong() == queueItem.blockPos.asLong()) {
                System.out.println("We have tried to access a block ent from another dimension: " + pos);
                BlockState bs = queueItem.block.getDefaultState();
                BlockEntity be = BlockEntity.createFromTag(bs, queueItem.blockEntity);

                if (be != null) {
                    ((BlockEntityMixin)be).setCachedState(bs);
                    ((BlockEntityMixin)be).setWorld(new ProxyClientWorld(MinecraftClient.getInstance().world));
                }

                callbackInfo.setReturnValue(be);
            }
        }
    }
}
