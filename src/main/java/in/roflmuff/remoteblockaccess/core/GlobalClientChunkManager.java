package in.roflmuff.remoteblockaccess.core;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class GlobalClientChunkManager {
    public static GlobalClientChunkManager Instance;
    private HashMap<Identifier, LocalChunkManager> chunkManagers = new HashMap<>();

    public GlobalClientChunkManager() {
        GlobalClientChunkManager.Instance = this;
    }

    public void initializeForWorld(Identifier dimension) {
        if (!chunkManagers.containsKey(dimension)) {
            chunkManagers.put(dimension, new LocalChunkManager());
        }
    }

    public LocalChunkManager getLocalManager(ClientWorld world) {
        Identifier dimension = world.getRegistryKey().getValue();
        return chunkManagers.get(dimension);
    }

    public LocalChunkManager getLocalManager(Identifier dimension) {
        if (!chunkManagers.containsKey(dimension)) {
            initializeForWorld(dimension);
        }

        return chunkManagers.get(dimension);
    }
}
