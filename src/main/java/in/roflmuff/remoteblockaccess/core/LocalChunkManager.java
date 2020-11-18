package in.roflmuff.remoteblockaccess.core;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

public class LocalChunkManager {
    private final ClientWorld world;
    private final ClientChunkManager clientChunkManager;

    private final Long2ObjectMap<WorldChunk> fakeChunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
    private final Set<Long> chunksToSave = new HashSet<>();

    public static LocalChunkManager Instance;

    public LocalChunkManager(ClientWorld world, ClientChunkManager clientChunkManager) {
        this.world = world;
        this.clientChunkManager = clientChunkManager;
        LocalChunkManager.Instance = this;
    }

    public WorldChunk getChunk(int x, int z) {
        if (fakeChunks.containsKey(ChunkPos.toLong(x, z))) {
            return fakeChunks.get(ChunkPos.toLong(x, z));
        }
        return null;
    }

    public void saveChunk(WorldChunk chunk) {
        if (chunksToSave.contains(chunk.getPos().toLong())) {
            fakeChunks.put(chunk.getPos().toLong(), chunk);
            System.out.println("Saved chunk, total chunks: " + fakeChunks.size());
        }
    }

    public void registerChunkForListening(WorldChunk chunk) {
        chunksToSave.add(chunk.getPos().toLong());
    }
}
