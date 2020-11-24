package in.roflmuff.remoteblockaccess.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import in.roflmuff.remoteblockaccess.screen.widget.IHandleMouseEvents;
import in.roflmuff.remoteblockaccess.screen.widget.button.ITooltipButton;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

public abstract class BaseScreen<T extends ScreenHandler> extends HandledScreen<T> {
    public static final int Z_LEVEL_ITEMS = 100;
    public static final int Z_LEVEL_TOOLTIPS = 500;
    public static final int Z_LEVEL_QTY = 300;

    private static final Map<Class, Queue<Consumer>> ACTIONS = new HashMap<>();

    private int xSize = 176;
    private int ySize = 176;

    private int delayTicks;

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    public BlockEntity be;

    public BaseScreen(T container, PlayerEntity player, Text title) {
        super(container, player.inventory, title);
    }

    private void runActions() {
        runActions(getClass());
        runActions(HandledScreen.class);
    }

    private void runActions(Class clazz) {
        Queue<Consumer> queue = ACTIONS.get(clazz);

        if (queue != null && !queue.isEmpty()) {
            Consumer callback;
            while ((callback = queue.poll()) != null) {
                callback.accept(this);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        for (Element child : this.children) {
            if (child instanceof IHandleMouseEvents) {
                if (child.mouseScrolled(d, e, amount)) return true;
            }
        }

        return super.mouseScrolled(d, e, amount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for(Element child: this.children) {
            if (child instanceof IHandleMouseEvents) {
                child.mouseMoved(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        for (Element child: this.children) {
            if (child.charTyped(chr, keyCode)) return true;
        }

        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child: this.children) {
            if (child instanceof TextFieldWidget) {
                if (child.keyPressed(keyCode, scanCode, modifiers) || ((TextFieldWidget)child).isActive()) return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        onPreInit();
        super.init();
        onPostInit(x, y);
        runActions();
    }

    protected void onPreInit() {}
    public abstract void onPostInit(int x, int y);
    public abstract void tick(int x, int y);

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        //renderBackground();

        super.render(matrices, mouseX, mouseY, partialTicks);

        this.children.stream()
                .filter(child -> child instanceof Drawable)
                .map(child -> (Drawable)child)
                .forEach(child -> child.render(matrices, mouseX, mouseY, partialTicks));

        this.buttons.stream()
                .filter(button -> button.isMouseOver(mouseX, mouseY) && button instanceof ITooltipButton)
                .forEach(button -> renderTooltip(matrices, ((ITooltipButton) button).getTooltip(), mouseX, mouseY));

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        runActions();
        tick(x, y);

        if (delayTicks > 0) {
            delayTicks--;
        }
    }

    @Override
    public void removed() {
        super.removed();
    }

    protected final void sendSettingsDelayed(int delayTicks) {
        this.delayTicks = delayTicks;
    }

    public void bindTexture(Identifier filename) {
        this.getMinecraft().getTextureManager().bindTexture(filename);
    }

    protected void drawTitle(MatrixStack matrixStack) {
        drawCentredString(matrixStack, I18n.translate(be.getCachedState().getBlock().getTranslationKey()), 6, 4210752);
    }

    public void drawCentredString(MatrixStack matrixStack, String string, int y, int colour) {
        drawString(matrixStack, string, (backgroundWidth / 2 - getTextRenderer().getWidth(string) / 2), y, colour);
    }

    protected void drawCentredString(MatrixStack matrixStack, String string, int y, int colour, int modifier) {
        drawString(matrixStack, string, (backgroundWidth / 2 - (getTextRenderer().getWidth(string)) / 2) + modifier, y, colour);
    }

    public void drawString(MatrixStack matrixStack, String string, int x, int y, int colour) {
        getTextRenderer().draw(matrixStack, string, x, y , colour);
        RenderSystem.color4f(1, 1, 1, 1);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderString(MatrixStack matrixStack, int x, int y, String message) {
        renderString(matrixStack, x, y, message, 0x3F3F3F);
    }

    public void renderString(MatrixStack matrixStack, int x, int y, String message, int color) {
        getTextRenderer().draw(matrixStack, message, x, y, color);
    }

    public TextRenderer getTextRenderer() {
        return this.getMinecraft().textRenderer;
    }

    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    public int getGuiLeft(){return x;}
    public int getGuiTop(){return y;}

    public void renderItem(MatrixStack matrixStack, int x, int y, ItemStack stack) {
        renderItem(matrixStack, x, y, stack, false, null, 0);
    }

    public void renderQuantity(MatrixStack matrixStack, int x, int y, String qty, int color) {
        boolean large = false;

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, Z_LEVEL_QTY);

        if (!large) {
            RenderSystem.scalef(0.5f, 0.5f, 1);
        }

        getTextRenderer().drawWithShadow(matrixStack, qty, (large ? 16 : 30) - getTextRenderer().getWidth(qty), large ? 8 : 22, color);
        RenderSystem.popMatrix();
    }

    public void renderItem(MatrixStack matrixStack, int x, int y, ItemStack stack, boolean overlay, String text, int textColor) {
        try {
            setZOffset(Z_LEVEL_ITEMS);

            itemRenderer.zOffset = Z_LEVEL_ITEMS;

            itemRenderer.renderGuiItemIcon(stack, x, y);

            if (overlay) {
                itemRenderer.renderGuiItemOverlay(getTextRenderer(), stack, x, y, "");
            }

            setZOffset(0);
            itemRenderer.zOffset = 0;

            if (text != null) {
                renderQuantity(matrixStack, x, y, text, textColor);
            }
        } catch (Throwable t) {

        }
    }

    public static <T> void executeLater(Class<T> clazz, Consumer<T> callback) {
        Queue<Consumer> queue = ACTIONS.get(clazz);

        if (queue == null) {
            ACTIONS.put(clazz, queue = new ArrayDeque<>());
        }

        queue.add(callback);
    }

    public static void executeLater(Consumer<HandledScreen> callback) {
        executeLater(HandledScreen.class, callback);
    }
}

