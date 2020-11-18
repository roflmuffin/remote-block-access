package in.roflmuff.remoteblockaccess.render;


import com.mojang.blaze3d.systems.RenderSystem;
import in.roflmuff.remoteblockaccess.client.ModRenderTypes;
import in.roflmuff.remoteblockaccess.core.ModItems;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import in.roflmuff.remoteblockaccess.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TargetRenderer {

    private static final float BOX_SIZE = 0.4f;
    private static ItemStack lastStack = ItemStack.EMPTY;
    private static List<BlockPos> blockPositions = null;

    public static void clientTick(MinecraftClient minecraftClient) {
        if (MinecraftClient.getInstance().player != null) {
            ItemStack curItem = MinecraftClient.getInstance().player.getMainHandStack();

            if (!ItemUtils.isItemEqual(curItem, lastStack, true)) {
                lastStack = curItem.copy();
                updatePositions(curItem);
            }
        }
    }

    private static void updatePositions(ItemStack itemStack) {
        if (itemStack.getItem() == ModItems.REMOTE_ACCESS_ITEM) {
            blockPositions = new ArrayList<>();
            List<RemoteBlockConfiguration> targets = RemoteAccessItem.getTargets(itemStack, false);
            if (targets.size() > 0) {
                List<BlockPos> blockPosList = targets.stream().map(x -> x.globalPos.getPos()).collect(Collectors.toList());
                blockPositions.addAll(blockPosList);
            }
        } else {
            blockPositions = null;
        }
    }

    public static ActionResult renderWorld(MatrixStack matrixStack) {
        if (blockPositions != null) {

            matrixStack.push();
            Vec3d projectedView = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

            Tessellator tessellator = Tessellator.getInstance();

            float start = (1 - BOX_SIZE) / 2.0f;

            for(BlockPos pos : blockPositions) {
                matrixStack.push();
                matrixStack.translate(pos.getX() + start, pos.getY() + start, pos.getZ() + start);
                Matrix4f posMat = matrixStack.peek().getModel();
                int color = 0x00ff00;
                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0xFF00) >> 8;
                int b = color & 0xFF;
                int alpha = 100;
                RenderSystem.disableDepthTest();

                VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                VertexConsumer bufferBuilder = immediate.getBuffer(ModRenderTypes.BLOCK_HILIGHT_FACE);
                //BufferBuilder bufferBuilder = tessellator.getBuffer();


//                bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat,0, 0, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, 0, 0, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(r, g, b, alpha).next();

                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(r, g, b, alpha).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(r, g, b, alpha).next();

                immediate.draw();

                bufferBuilder = immediate.getBuffer(ModRenderTypes.BLOCK_HILIGHT_LINE);

                //tessellator.draw();

                //bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(posMat, 0, 0, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(64, 64, 64, 80).next();

                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(64, 64, 64, 80).next();

                bufferBuilder.vertex(posMat, 0, 0, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(64, 64, 64, 80).next();

                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(64, 64, 64, 80).next();

                bufferBuilder.vertex(posMat, 0, 0, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, 0, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, 0, BOX_SIZE).color(64, 64, 64, 80).next();

                bufferBuilder.vertex(posMat, 0, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, BOX_SIZE).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, BOX_SIZE, BOX_SIZE, 0).color(64, 64, 64, 80).next();
                bufferBuilder.vertex(posMat, 0, BOX_SIZE, 0).color(64, 64, 64, 80).next();

                immediate.draw();

                //tessellator.draw();
                matrixStack.pop();
            }

            matrixStack.pop();
        }

        return ActionResult.PASS;
    }
}
