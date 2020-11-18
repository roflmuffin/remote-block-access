package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
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
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.io.IOException;
import java.rmi.Remote;

public class ModNetwork {

    public static Identifier MY_PACKET;
    public static Identifier LOAD_CHUNK_REQUEST;
    public static Identifier OPEN_GUI_REQUEST;

    private static Identifier registerPacket(Identifier identifier, PacketConsumer consumer, boolean recipientIsServer) {
        if (recipientIsServer) {
            ServerSidePacketRegistry.INSTANCE.register(identifier, consumer);
        } else {
            ClientSidePacketRegistry.INSTANCE.register(identifier, consumer);
        }

        return identifier;
    }

    public static void register() {
        MY_PACKET = registerPacket(new Identifier(RemoteBlockAccess.MOD_ID, "my_packet"), (context, packetByteBuf) -> {
            System.out.println("My Packet Received!");
            ChunkDataS2CPacket packet = new ChunkDataS2CPacket();
            try {
                packet.read(packetByteBuf);
                int k = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, false);

        LOAD_CHUNK_REQUEST = registerPacket(new Identifier(RemoteBlockAccess.MOD_ID, "load_chunk_request"), (packetContext, packetByteBuf) -> {
            ServerPlayerEntity sender = (ServerPlayerEntity)packetContext.getPlayer();
            BlockPos blockPos = packetByteBuf.readBlockPos();
            BlockHitResult hitResult = packetByteBuf.readBlockHitResult();

            System.out.println("Received request for Block Pos: " + blockPos + " from : " + sender);

            packetContext.getTaskQueue().execute(() -> {
                ServerWorld world = (ServerWorld) sender.world;
                WorldChunk chunk = (WorldChunk) world.getChunk(blockPos);

                LocalChunkManager.Instance.registerChunkForListening(chunk);

                ChunkDataS2CPacket packet = new ChunkDataS2CPacket((WorldChunk) chunk, 65535);
                sender.networkHandler.sendPacket(packet);

                RemoteBlockConfiguration configuration = RemoteAccessItem.getTarget(sender.getStackInHand(Hand.MAIN_HAND));
                if (configuration != null) {
                    RemoteAccessItem.mimicUseAction(configuration, sender);
                }

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(blockPos);
                buf.writeBlockHitResult(hitResult);
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(sender, ModNetwork.OPEN_GUI_REQUEST, buf);
            });
        }, true);

        OPEN_GUI_REQUEST = registerPacket(new Identifier(RemoteBlockAccess.MOD_ID, "open_gui_request"), (packetContext, packetByteBuf) -> {
            PlayerEntity player = packetContext.getPlayer();
            BlockPos blockPos = packetByteBuf.readBlockPos();
            BlockHitResult hitResult = packetByteBuf.readBlockHitResult();

            packetContext.getTaskQueue().execute(() -> {
                RemoteBlockConfiguration configuration = RemoteAccessItem.getTarget(player.getStackInHand(Hand.MAIN_HAND));
                if (configuration != null) {
                    RemoteAccessItem.mimicUseAction(configuration, player);
                }
            });
        }, false);
    }
}
