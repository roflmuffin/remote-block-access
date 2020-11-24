package in.roflmuff.remoteblockaccess.items;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.util.MiscUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class RemoteBlockConfiguration {

    public GlobalPos globalPos;
    public final String translationKey;
    public final BlockHitResult hitResult;

    public RemoteBlockConfiguration(GlobalPos globalPos, String translationKey, BlockHitResult hitResult) {
        this.globalPos = globalPos;
        this.translationKey = translationKey;
        this.hitResult = hitResult;
    }

    public RemoteBlockConfiguration(GlobalPos gPos, Direction face) {
        this(gPos, "", null);
    }

    public RemoteBlockConfiguration(GlobalPos gPos) {
        this(gPos, null);
    }

    public CompoundTag toNBT() {
        CompoundTag ext = new CompoundTag();
        ext.put("Pos", MiscUtil.serializeGlobalPos(globalPos));
        ext.putString("InvName", translationKey);
        ext.put("BlockHitResult", MiscUtil.serializeBlockHitResult(hitResult));

        return ext;
    }

    public static RemoteBlockConfiguration fromNBT(CompoundTag nbt) {
        GlobalPos gPos = MiscUtil.deserializeGlobalPos(nbt.getCompound("Pos"));
        BlockHitResult hitResult = MiscUtil.deserializeBlockHitResult(nbt.getCompound("BlockHitResult"));

        RemoteBlockConfiguration RemoteBlockConfiguration= new RemoteBlockConfiguration(gPos, nbt.getString("InvName"), hitResult);

        return RemoteBlockConfiguration;
    }

    public boolean isSameWorld(World world) {
        return globalPos.getDimension() == world.getRegistryKey();
    }

    public Optional<Inventory> getInventory(World w) {
        return isSameWorld(w) ? getInventoryFor(w) : Optional.empty();
    }

    public Optional<Inventory> getInventory() {
        return getInventoryFor(MiscUtil.getWorldForGlobalPos(globalPos));
    }

    private Optional<Inventory> getInventoryFor(World w) {
        BlockPos pos = globalPos.getPos();
        if (w == null /*|| !w.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)*/)
            return Optional.empty();
        BlockEntity te = w.getBlockEntity(pos);
        return te == null ? Optional.empty() : Optional.of((Inventory) te);
    }

    public ServerWorld getServerWorld() {
        return RemoteBlockAccess.getCurrentServer().getWorld(globalPos.getDimension());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteBlockConfiguration)) return false;
        RemoteBlockConfiguration that = (RemoteBlockConfiguration) o;
        return globalPos.equals(that.globalPos);// && face == that.face;
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalPos);
    }

    @Override
    public String toString() {
        return MiscUtil.locToString(globalPos.getDimension(), globalPos.getPos());
    }

    public MutableText getTextComponent(boolean includeDimension) {
        return new TranslatableText(" ")
                .append(String.format("[%s]", MiscUtil.xlate(translationKey).getString()))
                .formatted(Formatting.WHITE)
                .append(includeDimension ? " @ " : "")
                .append(includeDimension ? this.toString() : "")
                //.append(MiscUtil.locToString(globalPos.getDimension().getId(), globalPos.getPos()) + " " + face)
                .formatted(Formatting.AQUA);
    }

    public int getSize(World world) {
        if (world instanceof ServerWorld) {
            return this.getInventory().isPresent() ? this.getInventory().get().size() : 0;
        }

        return this.getInventoryFor(world).isPresent() ? this.getInventoryFor(world).get().size() : 0;
    }
}
