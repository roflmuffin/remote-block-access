package in.roflmuff.remoteblockaccess.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BaseItem extends Item {
    public BaseItem() {
        super(new Item.Settings().group(ItemGroup.MISC));
    }
}
