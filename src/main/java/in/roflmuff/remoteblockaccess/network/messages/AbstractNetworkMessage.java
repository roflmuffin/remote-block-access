package in.roflmuff.remoteblockaccess.network.messages;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AbstractNetworkMessage {

    private Identifier _identifier;

    public Identifier getIdentifier() {
        return _identifier;
    }

    public AbstractNetworkMessage(String name) {
        _identifier = new Identifier(RemoteBlockAccess.MOD_ID, name);
    }

    abstract void encode(PacketByteBuf buf);
    abstract void decode(PacketByteBuf buf);

    public void sendToServer() {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        encode(passedData);
        ClientSidePacketRegistry.INSTANCE.sendToServer(_identifier, passedData);
    }

    public void sendToClient(PlayerEntity player) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        encode(passedData);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, _identifier, passedData);
    }

    public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
        this.decode(packetByteBuf);
        context.getTaskQueue().execute(() -> {
            this.execute(context);
        });
    }

    abstract void execute(PacketContext context);
}
