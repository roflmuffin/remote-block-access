package in.roflmuff.remoteblockaccess.network.messages;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AbstractNetworkMessage {

    private Identifier _identifier;

    public AbstractNetworkMessage(Identifier identifier) {
        _identifier = identifier;
    }

    abstract void encode(PacketByteBuf buf);

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
}
