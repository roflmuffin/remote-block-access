package in.roflmuff.remoteblockaccess.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.screen.BaseScreen;
import in.roflmuff.remoteblockaccess.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ScrollbarWidget implements Drawable, Element, IHandleMouseEvents {
    private static final int SCROLLER_HEIGHT = 15;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean enabled = false;

    private int offset;
    private int maxOffset;

    private boolean clicked = false;

    private List<ScrollbarWidgetListener> listeners = new LinkedList<>();

    private BaseScreen<?> screen;

    public ScrollbarWidget(BaseScreen<?> screen, int x, int y, int width, int height) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addListener(ScrollbarWidgetListener listener) {
        listeners.add(listener);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        screen.bindTexture(new Identifier(RemoteBlockAccess.MOD_ID, "textures/gui/icons.png"));
        screen.drawTexture(matrixStack, screen.getGuiLeft() + x, screen.getGuiTop() + y + (int) Math.min(height - SCROLLER_HEIGHT, (float) offset / (float) maxOffset * (float) (height - SCROLLER_HEIGHT)), isEnabled() ? 232 : 244, 0, 12, 15);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        mx -= screen.getGuiLeft();
        my -= screen.getGuiTop();

        if (button == 0 && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            updateOffset(my);
            clicked = true;
            return true;
        }

        return false;
    }

    @Override
    public void mouseMoved(double mx, double my) {
        mx -= screen.getGuiLeft();
        my -= screen.getGuiTop();

        if (clicked && RenderUtils.inBounds(x, y, width, height, mx, my)) {
            updateOffset(my);
        }
    }

    private void updateOffset(double my) {
        setOffset((int) Math.floor((float) (my - y) / (float) (height - SCROLLER_HEIGHT) * (float) maxOffset));
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (clicked) {
            clicked = false;

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (isEnabled()) {
            setOffset(offset + Math.max(Math.min(-(int) scrollDelta, 1), -1));

            return true;
        }

        return false;
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;

        if (offset > maxOffset) {
            this.offset = Math.max(0, maxOffset);
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        int oldOffset = this.offset;

        if (offset >= 0 && offset <= maxOffset) {
            this.offset = offset;

            listeners.forEach(l -> l.onOffsetChanged(oldOffset, offset));
        }
    }
}
