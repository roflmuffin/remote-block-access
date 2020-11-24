package in.roflmuff.remoteblockaccess.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientChunkQueue {
    public static ClientChunkQueue Instance;

    private final Queue<ChunkQueueItem> chunkQueueItems = new ArrayDeque<>();

    public ClientChunkQueue() {
        Instance = this;
    }

    public void push(RegistryKey<World> dimension, BlockPos pos) {
        chunkQueueItems.add(new ChunkQueueItem(dimension, pos));
        System.out.println("Added to local block entity queue: " + dimension + " | " + pos);
    }

    public ChunkQueueItem pop() {
        ChunkQueueItem item = chunkQueueItems.poll();
        System.out.println("Removed from local block entity queue: " + item.dimension + " | " + item.blockPos);
        return item;
    }

    public ChunkQueueItem tryPop() {
        if (chunkQueueItems.size() > 0) {
            return chunkQueueItems.poll();
        }
        return null;
    }

    public ChunkQueueItem peek() {
        return chunkQueueItems.peek();
    }
}
