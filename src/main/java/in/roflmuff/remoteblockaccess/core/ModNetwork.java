package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import in.roflmuff.remoteblockaccess.network.messages.AbstractNetworkMessage;
import in.roflmuff.remoteblockaccess.network.messages.BlockEntityDataMessage;
import in.roflmuff.remoteblockaccess.network.messages.LoadChunkMessage;
import in.roflmuff.remoteblockaccess.network.messages.OpenGuiMessage;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.io.IOException;
import java.rmi.Remote;
import java.util.function.Supplier;

public class ModNetwork {

    public static Identifier BLOCK_ENTITY_DATA;
    public static Identifier LOAD_CHUNK_REQUEST;
    public static Identifier OPEN_GUI_REQUEST;

    private static Identifier registerPacket(String name, PacketConsumer consumer, boolean recipientIsServer) {
        Identifier identifier = new Identifier(RemoteBlockAccess.MOD_ID, name);
        if (recipientIsServer) {
            ServerSidePacketRegistry.INSTANCE.register(identifier, consumer);
        } else {
            ClientSidePacketRegistry.INSTANCE.register(identifier, consumer);
        }

        return identifier;
    }

    private static Identifier registerPacket(Supplier<AbstractNetworkMessage> messageSupplier, boolean recipientIsServer) {
        AbstractNetworkMessage message = messageSupplier.get();
        PacketConsumer consumer = (packetContext, packetByteBuf) -> {
            AbstractNetworkMessage newMessage = messageSupplier.get();
            newMessage.accept(packetContext, packetByteBuf);
        };

        if (recipientIsServer) {
            ServerSidePacketRegistry.INSTANCE.register(message.getIdentifier(), consumer);
        } else {
            ClientSidePacketRegistry.INSTANCE.register(message.getIdentifier(), consumer);
        }

        return message.getIdentifier();
    }

    public static void register() {
        LOAD_CHUNK_REQUEST = registerPacket(LoadChunkMessage::new, true);
        OPEN_GUI_REQUEST = registerPacket(OpenGuiMessage::new, false);
        BLOCK_ENTITY_DATA = registerPacket(BlockEntityDataMessage::new, false);
    }
}
