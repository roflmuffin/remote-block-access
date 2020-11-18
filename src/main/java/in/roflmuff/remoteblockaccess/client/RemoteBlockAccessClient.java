package in.roflmuff.remoteblockaccess.client;

import in.roflmuff.remoteblockaccess.event.client.WorldRendererRenderCallback;
import in.roflmuff.remoteblockaccess.render.TargetRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.render.WorldRenderer;

@Environment(EnvType.CLIENT)
public class RemoteBlockAccessClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(TargetRenderer::clientTick);
        WorldRendererRenderCallback.EVENT.register(TargetRenderer::renderWorld);
    }
}
