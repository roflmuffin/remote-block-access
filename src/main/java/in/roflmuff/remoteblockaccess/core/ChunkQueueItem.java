package in.roflmuff.remoteblockaccess.core;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ChunkQueueItem {
    public RegistryKey<World> dimension;
    public BlockPos blockPos;
    public CompoundTag blockEntity;
    public Block block;

    public ChunkQueueItem(RegistryKey<World> dimension, BlockPos blockPos) {
        this.dimension = dimension;
        this.blockPos = blockPos;
    }
}
