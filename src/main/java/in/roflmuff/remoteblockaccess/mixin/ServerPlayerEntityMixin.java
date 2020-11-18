package in.roflmuff.remoteblockaccess.mixin;

import com.mojang.authlib.GameProfile;
import in.roflmuff.remoteblockaccess.core.ModItems;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow public abstract void incrementScreenHandlerSyncId();

    @Shadow public int screenHandlerSyncId;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Redirect(method="tick",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandler;canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z"
    ))
    private boolean onScreenHandlerCanUseTick(ScreenHandler screenHandler, PlayerEntity player) {
        if (player.getMainHandStack().getItem() == ModItems.REMOTE_ACCESS_ITEM) return true;

        return screenHandler.canUse(player);
        //if (player.getMainHandStack().getItem() == ModItems.REMOTE_ACCESS_ITEM) return true;
    }


}
