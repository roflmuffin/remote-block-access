package in.roflmuff.remoteblockaccess.network.messages;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.core.ModNetwork;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
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
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;

public class LoadChunkMessage extends AbstractNetworkMessage {

    BlockPos blockPos;
    BlockHitResult hitResult;
    RegistryKey<World> worldKey;

    public LoadChunkMessage() {
        super("load_chunk_message");
    }

    public LoadChunkMessage(BlockPos pos, BlockHitResult hitResult, RegistryKey<World> worldKey) {
        super("load_chunk_message");
        this.blockPos = pos;
        this.hitResult = hitResult;
        this.worldKey = worldKey;
    }

    @Override
    void decode(PacketByteBuf buf) {
        blockPos = buf.readBlockPos();
        hitResult = buf.readBlockHitResult();
        worldKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(buf.readString()));
    }

    @Override
    void encode(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeBlockHitResult(hitResult);
        buf.writeString(worldKey.getValue().toString());
    }

    @Override
    protected void execute(PacketContext context) {
        ServerWorld world = RemoteBlockAccess.getCurrentServer().getWorld(worldKey);
        WorldChunk chunk = (WorldChunk) world.getChunk(blockPos);
        ServerPlayerEntity player =  ((ServerPlayerEntity)context.getPlayer());

        ChunkDataS2CPacket packet = new ChunkDataS2CPacket((WorldChunk) chunk, 65535);
        player.networkHandler.sendPacket(packet);

        List<RemoteBlockConfiguration> targets = RemoteAccessItem.getTargets(player.getStackInHand(Hand.MAIN_HAND), false);
        for (RemoteBlockConfiguration target : targets) {
            if (target != null) {
                RemoteAccessItem.mimicUseAction(world, target, player);
            }
        }

        OpenGuiMessage message = new OpenGuiMessage(blockPos, hitResult);
        message.sendToClient(player);
    }

}
