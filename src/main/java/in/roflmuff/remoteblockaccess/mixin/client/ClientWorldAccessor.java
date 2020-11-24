package in.roflmuff.remoteblockaccess.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)

public interface ClientWorldAccessor {
    @Accessor
    public ClientPlayNetworkHandler getNetHandler();

    @Accessor
    public ClientWorld.Properties getClientWorldProperties();

    @Accessor
    public WorldRenderer getWorldRenderer();
}
