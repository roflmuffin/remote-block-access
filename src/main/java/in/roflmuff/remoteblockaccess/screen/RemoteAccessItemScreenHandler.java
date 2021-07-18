package in.roflmuff.remoteblockaccess.screen;

import in.roflmuff.remoteblockaccess.core.ModScreens;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteBlockConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoteAccessItemScreenHandler extends ScreenHandler {
    private final List<RemoteBlockConfiguration> targets;
    private final ItemStack useItem;

    public RemoteAccessItemScreenHandler(int syncId, List<RemoteBlockConfiguration> targets) {
       super(ModScreens.REMOTE_ACCESS_ITEM_SCREEN, syncId);
       this.targets = targets;
       this.useItem = null;
   }

    public RemoteAccessItemScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(ModScreens.REMOTE_ACCESS_ITEM_SCREEN, syncId);
        int n;
        int m;
        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + (n + 1) * 9, 8 + m * 18, 104 + n * 18));
            }
        }

        for(n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 162));
        }

        useItem = playerInventory.getMainHandStack();
        this.targets = RemoteAccessItem.getTargets(useItem, false);
        int k = 0;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < targets.size()) {
            RemoteBlockConfiguration target = targets.get(id);
            if (useItem.getItem() instanceof RemoteAccessItem) {
                ((RemoteAccessItem)useItem.getItem()).useForTarget(target, player.world, player);
            }
        }
        return super.onButtonClick(player, id);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public List<RemoteBlockConfiguration> getTargets() {
        return this.targets;
    }
}
