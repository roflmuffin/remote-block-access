package in.roflmuff.remoteblockaccess.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class ModRenderTypes extends RenderLayer {
    public ModRenderTypes(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final LineWidth THIN_LINE = new LineWidth(OptionalDouble.of(1.0D));

    public static final RenderLayer BLOCK_HILIGHT_FACE = of("block_hilight",
            VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderLayer.NO_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .depthTest(ALWAYS_DEPTH_TEST)
                    .cull(DISABLE_CULLING)
                    .lightmap(DISABLE_LIGHTMAP)
                    .writeMaskState(COLOR_MASK)
                    .build(false)
    );

    public static final RenderLayer BLOCK_HILIGHT_LINE = of("block_hilight_line",
            VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
            RenderLayer.MultiPhaseParameters.builder()
                    .lineWidth(THIN_LINE)
                    .layering(RenderLayer.NO_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .depthTest(ALWAYS_DEPTH_TEST)
                    .cull(DISABLE_CULLING)
                    .lightmap(DISABLE_LIGHTMAP)
                    .writeMaskState(COLOR_MASK)
                    .build(false)
    );
}
