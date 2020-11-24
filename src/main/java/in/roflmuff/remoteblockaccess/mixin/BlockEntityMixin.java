package in.roflmuff.remoteblockaccess.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface BlockEntityMixin {
    @Accessor("cachedState")
    public void setCachedState(BlockState blockState);

    @Accessor
    public void setWorld(World world);
}
