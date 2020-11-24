package in.roflmuff.remoteblockaccess.mixin;

import in.roflmuff.remoteblockaccess.core.ModItems;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {


    @Redirect(method="tick",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandler;canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z"
            ))
    private boolean onScreenHandlerCanUseTick(ScreenHandler screenHandler, PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof RemoteAccessItem) return true;

        return screenHandler.canUse(player);
    }
}
