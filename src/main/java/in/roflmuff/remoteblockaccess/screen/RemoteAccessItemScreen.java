package in.roflmuff.remoteblockaccess.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import in.roflmuff.remoteblockaccess.screen.widget.ScrollbarWidget;
import in.roflmuff.remoteblockaccess.screen.widget.SearchWidget;
import in.roflmuff.remoteblockaccess.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class RemoteAccessItemScreen extends BaseScreen<ScreenHandler> implements IScreenInfoProvider {

    private static final Identifier TEXTURE = new Identifier(RemoteBlockAccess.MOD_ID, "textures/gui/remote_access_item_screen.png");
    private float scrollAmount;
    private int scrollOffset;
    private boolean ignoreTypedCharacter;
    private ScrollbarWidget scrollbarWidget;
    private SearchWidget searchWidget;
    private String searchFilter;
    private boolean mouseClicked;

    public RemoteAccessItemScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory.player, title);
        this.backgroundWidth = 175;
        this.backgroundHeight = 186;
        this.playerInventoryTitleY = this.getYPlayerInventory();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        this.scrollbarWidget = new ScrollbarWidget(this, 156, 35, 12, (getVisibleRows() * 18) - 1);

        int sx = x + 9;
        int sy = y + 23;
        if(searchWidget == null) {
            searchWidget = new SearchWidget(this.getTextRenderer(), sx, sy, 145, true);
            searchWidget.setChangedListener((value) ->{
                this.setSearchFilter(value);
                this.sendSettingsDelayed(5);
            });
        }
        else {
            searchWidget.x = sx;
            searchWidget.y = sy;
        }
        addButton(searchWidget);
        this.children.add(scrollbarWidget);
    }

    private void setSearchFilter(String value) {
        this.searchFilter = value;
    }

    @Override
    public void onPostInit(int x, int y) {

    }

    @Override
    public void tick(int x, int y) {
        updateScrollbar();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.renderOptionBackgrounds(matrices, mouseX, mouseY, x + 8, y + 35);
        this.renderOptionNames(matrices, x + 8, y + 35);
    }

    private void renderOptionBackgrounds(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y) {
        for (int i = 0; i < getVisibleRows(); i++) {
            int actualOffset = i + getCurrentOffset();
            if (actualOffset >= getFilteredTargets().size()) continue;

            RemoteBlockConfiguration target = getFilteredTargets().get(actualOffset);

            int offsetY = y + (i * 18);
            int backgroundOffset = this.backgroundHeight;

            if (RenderUtils.inBounds(x, offsetY, 145, 18, mouseX, mouseY)) {
                if (mouseClicked) { // Mouse is down
                    backgroundOffset += 18;
                } else {
                    backgroundOffset += 36;
                }
            }

            this.drawTexture(matrixStack, x, offsetY, 0, backgroundOffset, 145, 18);
        }
    }

    private void renderOptionNames(MatrixStack matrixStack, int x, int y) {
        for (int i = 0; i < getVisibleRows(); i++) {
            int actualOffset = i + getCurrentOffset();

            if (actualOffset >= getFilteredTargets().size()) continue;

            RemoteBlockConfiguration target = this.getFilteredTargets().get(actualOffset);

            int offsetY = y + (i * 18);

            //World world = new ProxyClientWorld((ClientWorld) this.playerInventory.player.world);
            World world = this.playerInventory.player.world;
            BlockState blockState = world.getBlockState(target.globalPos.getPos());
            ItemStack blockAsItemStack = new ItemStack(blockState.getBlock().asItem());
            this.renderItem(matrixStack, x + 3, offsetY + 1, blockAsItemStack);
            this.textRenderer.draw(matrixStack, new TranslatableText(target.translationKey).getString(), x + 22, offsetY + 5, 0x161616);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        if (this.getFilteredTargets().size() > 0) {

            for (int i = 0; i < getVisibleRows(); i++) {
                int actualOffset = i + getCurrentOffset();
                if (actualOffset >= getFilteredTargets().size()) continue;

                RemoteBlockConfiguration target = getFilteredTargets().get(actualOffset);

                int thisX = x + 8;
                int offsetY = y + 35 + (i * 18);
                int backgroundOffset = this.backgroundHeight;

                if (RenderUtils.inBounds(thisX, offsetY, 145, 18, mouseX, mouseY)) {
                    int unfilteredIndex = getTargets().indexOf(target);
                    this.client.interactionManager.clickButton((this.handler).syncId, unfilteredIndex);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void updateScrollbar() {
        scrollbarWidget.setEnabled(getRows() > getVisibleRows());
        scrollbarWidget.setMaxOffset(getRows() - getVisibleRows());
    }

    @Override
    public int getVisibleRows() {
        return 3;
    }

    @Override
    public int getRows() {
        return this.getFilteredTargets().size();
    }

    @Override
    public int getCurrentOffset() {
        return scrollbarWidget != null ? scrollbarWidget.getOffset() : 0;
    }

    @Override
    public int getTopHeight() {
        return 21;
    }

    @Override
    public int getBottomHeight() {
        return 96;
    }

    @Override
    public int getYPlayerInventory() {
        int yp = getTopHeight() + (getVisibleRows() * 18);
        yp += 16;

        return yp;
    }

    @Override
    public RemoteAccessItemScreenHandler getScreenHandler() {
        return (RemoteAccessItemScreenHandler) super.getScreenHandler();
    }

    private List<RemoteBlockConfiguration> getTargets() {
        return getScreenHandler().getTargets().stream().filter(e -> {
            return e.isValidForWorld(playerInventory.player.world);
        }).collect(Collectors.toList());
    }

    private List<RemoteBlockConfiguration> getFilteredTargets() {
        if (searchFilter == null || searchFilter.isEmpty() || searchFilter.trim().isEmpty()) return getTargets();

        return getTargets().stream().filter((e) -> {
            String formattedName = new TranslatableText(e.translationKey).getString();
            return formattedName.toLowerCase().contains(searchFilter.toLowerCase());
        }).collect(Collectors.toList());
    }
}
