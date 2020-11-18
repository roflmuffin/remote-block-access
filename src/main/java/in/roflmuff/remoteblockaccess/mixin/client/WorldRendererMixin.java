package in.roflmuff.remoteblockaccess.mixin.client;

import in.roflmuff.remoteblockaccess.event.client.WorldRendererRenderCallback;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class WorldRendererMixin{

    @Inject(method="renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V",
            at=@At(value="INVOKE_STRING",
                    target="Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    args= { "ldc=hand" }
            )
    )
    private void onRenderWorldLast(float partialTicks, long nanoTime, MatrixStack matrixStack, CallbackInfo ci) {
        ActionResult result = WorldRendererRenderCallback.EVENT.invoker().render(matrixStack);
    }
}
