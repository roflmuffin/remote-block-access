package in.roflmuff.remoteblockaccess.network.messages;

import in.roflmuff.remoteblockaccess.core.ClientChunkQueue;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenGuiMessage extends AbstractNetworkMessage {

    private BlockPos blockPos;
    private BlockHitResult hitResult;

    public OpenGuiMessage() {
        super("open_gui");
    }

    public OpenGuiMessage(BlockPos blockPos, BlockHitResult hitResult) {
        super("open_gui");
        this.blockPos = blockPos;
        this.hitResult = hitResult;
    }

    @Override
    void encode(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeBlockHitResult(hitResult);

    }

    @Override
    void decode(PacketByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.hitResult = buf.readBlockHitResult();
    }

    @Override
    void execute(PacketContext context) {
        RemoteBlockConfiguration configuration = RemoteAccessItem.getTarget(context.getPlayer().getStackInHand(Hand.MAIN_HAND));
        if (configuration != null) {
            PlayerEntity player = context.getPlayer();
            ClientWorld world = (ClientWorld)player.world;
            RemoteAccessItem.mimicUseAction(world, configuration, context.getPlayer());
        }
    }
}
