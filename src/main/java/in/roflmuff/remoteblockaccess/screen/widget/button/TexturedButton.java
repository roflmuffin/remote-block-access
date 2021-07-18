package in.roflmuff.remoteblockaccess.screen.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TexturedButton extends PressableWidget implements ITooltipButton {

    static final Identifier TEXTURE = new Identifier(RemoteBlockAccess.MOD_ID, "textures/gui/widgets.png");

    protected final List<String> tooltip1;
    protected final Consumer<PressableWidget> pressable;
    private final int u;
    private final int v;

    public TexturedButton(int x, int y, int width, int height, Consumer<PressableWidget> pressable) {
        super(x, y, width, height, new LiteralText(""));
        this.tooltip1 = new ArrayList<>();
        this.pressable = pressable;
        this.u = 0;
        this.v = 0;
    }

    public TexturedButton(int x, int y, int u, int v, int width, int height, Consumer<PressableWidget> pressable) {
        super(x, y, width, height, new LiteralText(""));
        this.tooltip1 = new ArrayList<>();
        this.pressable = pressable;
        this.u = u;
        this.v = v;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getYImage(this.hovered);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            if (drawStandardBackground()) {
                drawTexture(matrixStack,this.x, this.y, u + (i * 18), v, this.width, this.height);
            }
            drawTexture(matrixStack,this.x, this.y, getTextureX(), getTextureY(), this.width, this.height);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (pressable != null) {
            pressable.accept(this);
        }
    }

    protected boolean drawStandardBackground() {
        return true;
    }

    protected abstract int getTextureX();

    protected abstract int getTextureY();

    @Override
    public List<Text> getTooltip() {
        return null;
    }
}
