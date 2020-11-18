package in.roflmuff.remoteblockaccess.util;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class MiscUtil {
    public static MutableText xlate(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static String locToString(RegistryKey<World> dim, BlockPos pos) {
        return String.format("DIM:%s [%d,%d,%d]", dim.getValue(), pos.getX(), pos.getY(), pos.getZ());
    }

    public static CompoundTag serializeGlobalPos(GlobalPos globalPos) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("pos",globalPos.getPos().asLong());
        tag.putString("dim", globalPos.getDimension().getValue().toString());
        return tag;
    }

    public static GlobalPos deserializeGlobalPos(CompoundTag tag) {
        return GlobalPos.create(
                RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("dim"))),
                BlockPos.fromLong(tag.getLong("pos"))
        );
    }

    public static CompoundTag serializeBlockHitResult(BlockHitResult blockHitResult) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("blockPos", blockHitResult.getBlockPos().asLong());
        tag.putDouble("hitPosX", blockHitResult.getPos().x);
        tag.putDouble("hitPosY", blockHitResult.getPos().y);
        tag.putDouble("hitPosZ", blockHitResult.getPos().z);
        tag.putInt("side", blockHitResult.getSide().getId());
        return tag;
    }

    public static BlockHitResult deserializeBlockHitResult(CompoundTag tag) {
        BlockPos blockPos = BlockPos.fromLong(tag.getLong("blockPos"));
        Vec3d hitPos = new Vec3d(tag.getDouble("hitPosX"), tag.getDouble("hitPosY"), tag.getDouble("hitPosZ"));
        Direction side = Direction.byId(tag.getInt("side"));

        return new BlockHitResult(hitPos, side, blockPos, false);
    }

    public static ServerWorld getWorldForGlobalPos(GlobalPos pos) {
        return RemoteBlockAccess.getCurrentServer().getWorld(pos.getDimension());
    }
}
