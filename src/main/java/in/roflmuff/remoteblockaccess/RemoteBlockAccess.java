package in.roflmuff.remoteblockaccess;

import in.roflmuff.remoteblockaccess.config.Configuration;
import in.roflmuff.remoteblockaccess.config.RemoteBlockAccessConfig;
import in.roflmuff.remoteblockaccess.core.ModItems;
import in.roflmuff.remoteblockaccess.core.ModNetwork;
import in.roflmuff.remoteblockaccess.core.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class RemoteBlockAccess implements ModInitializer {
    public static String MOD_ID = "remoteblockaccess";

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        new Configuration(RemoteBlockAccessConfig.class, "remoteblockaccess");

        ModItems.register();
        ModNetwork.register();
        ModSounds.register();

        ServerLifecycleEvents.SERVER_STARTED.register(RemoteBlockAccess::setServer);
    }

    public static void setServer(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static MinecraftServer getCurrentServer() {
        return server;
    }
}