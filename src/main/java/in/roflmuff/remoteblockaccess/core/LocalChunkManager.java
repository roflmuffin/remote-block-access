package in.roflmuff.remoteblockaccess.core;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

public class LocalChunkManager {

    private final Long2ObjectMap<WorldChunk> fakeChunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
    private final Set<Long> chunksToSave = new HashSet<>();

    public LocalChunkManager() {
    }

    public WorldChunk getChunk(int x, int z) {
        if (fakeChunks.containsKey(ChunkPos.toLong(x, z))) {
            return fakeChunks.get(ChunkPos.toLong(x, z));
        }
        return null;
    }

    public void saveChunk(WorldChunk chunk) {
        //if (chunksToSave.contains(chunk.getPos().toLong())) {
            fakeChunks.put(chunk.getPos().toLong(), chunk);
            System.out.println("Saved chunk in dimension:, total chunks: " + fakeChunks.size());
        //}
    }

    public void registerChunkForListening(WorldChunk chunk) {
        chunksToSave.add(chunk.getPos().toLong());
    }

    public void registerChunkForListening(ChunkPos pos) {
        chunksToSave.add(pos.toLong());
    }

    public void registerChunkForListening(BlockPos pos) {
        registerChunkForListening(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
    }
}
