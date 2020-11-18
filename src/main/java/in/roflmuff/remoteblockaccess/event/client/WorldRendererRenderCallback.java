package in.roflmuff.remoteblockaccess.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

public interface WorldRendererRenderCallback {
    Event<WorldRendererRenderCallback> EVENT = EventFactory.createArrayBacked(WorldRendererRenderCallback.class, (listeners) -> (matrixStack) -> {
        for (WorldRendererRenderCallback listener : listeners) {
            ActionResult result = listener.render(matrixStack);
            if(result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult render(MatrixStack matrixStack);
}
