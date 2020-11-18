package in.roflmuff.remoteblockaccess.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow @Final public boolean isClient;

    @Shadow @Final public List<BlockEntity> blockEntities;

    @Inject(method="getBlockEntity",
            at = @At("RETURN")
    )
    public void getBlockEntityMixin(BlockPos pos, CallbackInfoReturnable<BlockEntity> callbackInfo) {
        if (callbackInfo.getReturnValue() == null && this.isClient) {
            System.out.println(String.format("Got BE at %s, return val: %s", pos, callbackInfo.getReturnValue()));
        }
    }
}
