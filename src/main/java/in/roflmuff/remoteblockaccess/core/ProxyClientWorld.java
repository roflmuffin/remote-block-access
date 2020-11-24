package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.mixin.BlockEntityMixin;
import in.roflmuff.remoteblockaccess.mixin.client.ClientWorldAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ProxyClientWorld extends ClientWorld {
    private ClientWorld realWorld;

    public ProxyClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey<World> registryRef, DimensionType dimensionType, int loadDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed) {
        super(networkHandler, properties, registryRef, dimensionType, loadDistance, profiler, worldRenderer, debugWorld, seed);
    }

    public ProxyClientWorld(ClientWorld clientWorld) {
        super(((ClientWorldAccessor)clientWorld).getNetHandler(),
                ((ClientWorldAccessor)clientWorld).getClientWorldProperties(),
                clientWorld.getRegistryKey(),
                clientWorld.getDimension(),
                10,
                clientWorld.getProfilerSupplier(),
                ((ClientWorldAccessor)clientWorld).getWorldRenderer(),
                clientWorld.isDebugWorld(),
                0
        );
        realWorld = clientWorld;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        System.out.println("Block state requested...");
        return realWorld.getBlockState(pos);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return realWorld.getBlockEntity(pos);
    }

    /*    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        ChunkQueueItem queueItem = ClientChunkQueue.Instance.peek();
        if (queueItem != null && pos.asLong() == queueItem.blockPos.asLong()) {
            System.out.println("We have tried to access a block ent from another dimension: " + pos);
            BlockState bs = queueItem.block.getDefaultState();
            BlockEntity be = BlockEntity.createFromTag(bs, queueItem.blockEntity);

            if (be != null) {
                ((BlockEntityMixin)be).setCachedState(bs);
            }

            return be;
        }

        return realWorld.getBlockEntity(pos);
    }*/
}
