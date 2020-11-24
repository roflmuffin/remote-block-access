package in.roflmuff.remoteblockaccess.network.messages;

import in.roflmuff.remoteblockaccess.core.ClientChunkQueue;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEntityDataMessage extends AbstractNetworkMessage {

    private Identifier blockId;
    private CompoundTag blockEntity;

    public BlockEntityDataMessage(CompoundTag blockEntity, Identifier blockId) {
        super("block_entity_data");
        this.blockEntity = blockEntity;
        this.blockId = blockId;
    }

    public BlockEntityDataMessage() {
        super("block_entity_data");
    }

    @Override
    void encode(PacketByteBuf buf) {
        buf.writeCompoundTag(blockEntity);
        buf.writeString(blockId.toString());
    }

    @Override
    void decode(PacketByteBuf buf) {
        blockEntity = buf.readCompoundTag();
        blockId = new Identifier(buf.readString());
    }

    @Override
    void execute(PacketContext context) {
        ClientChunkQueue.Instance.peek().blockEntity = blockEntity;
        ClientChunkQueue.Instance.peek().block = Registry.BLOCK.get(blockId);

    }
}
