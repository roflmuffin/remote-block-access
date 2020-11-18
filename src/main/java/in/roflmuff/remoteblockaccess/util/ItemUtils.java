package in.roflmuff.remoteblockaccess.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class ItemUtils {

    public static boolean isItemEqual(final ItemStack a, final ItemStack b,
                                      final boolean matchNBT) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        return !matchNBT || ItemStack.areTagsEqual(a, b);
    }

    public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchNBT,
                                      boolean useTags) {
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        }
        if (isItemEqual(a, b, matchNBT)) {
            return true;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (useTags) {

            //TODO tags
        }
        return false;
    }
}