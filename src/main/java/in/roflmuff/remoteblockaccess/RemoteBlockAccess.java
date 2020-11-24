package in.roflmuff.remoteblockaccess;

import in.roflmuff.remoteblockaccess.config.Configuration;
import in.roflmuff.remoteblockaccess.config.RemoteBlockAccessConfig;
import in.roflmuff.remoteblockaccess.core.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RemoteBlockAccess implements ModInitializer {
    public static String MOD_ID = "remoteblockaccess";

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        new Configuration(RemoteBlockAccessConfig.class, "remoteblockaccess");

        ModItems.register();
        ModNetwork.register();
        ModSounds.register();
        ModRecipes.register();
        ModScreens.register();

        ServerLifecycleEvents.SERVER_STARTED.register(RemoteBlockAccess::setServer);
        AtomicInteger tickCount = new AtomicInteger();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount.getAndIncrement();
            if (tickCount.get() % 20 == 0) {
                ClientChunkQueue.Instance.tryPop();
            }
        });
    }

    public static void setServer(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static MinecraftServer getCurrentServer() {
        return server;
    }

    public static void clientOnly(Supplier<Runnable> runnable){
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            runnable.get().run();
        }
    }
}