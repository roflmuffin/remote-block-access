package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.BaseItem;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.items.RemoteAccessTier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModItems {
    public static RemoteAccessItem REMOTE_ACCESS_ITEM;
    public static RemoteAccessItem REMOTE_ACCESS_ITEM_MEDIUM;
    public static RemoteAccessItem REMOTE_ACCESS_ITEM_HIGH;
    public static RemoteAccessItem REMOTE_ACCESS_ITEM_EXTREME;
    private static Item REMOTE_CORE;

    private static <T extends Item> T register(String name, final Supplier<T> sup) {
        return Registry.register(Registry.ITEM, new Identifier(RemoteBlockAccess.MOD_ID, name), sup.get());
    }

    public static void register() {
        REMOTE_ACCESS_ITEM = register("remote_access_item_low", () -> new RemoteAccessItem(RemoteAccessTier.LOW));
        REMOTE_ACCESS_ITEM_MEDIUM = register("remote_access_item_medium", () -> new RemoteAccessItem(RemoteAccessTier.MEDIUM));
        REMOTE_ACCESS_ITEM_HIGH = register("remote_access_item_high", () -> new RemoteAccessItem(RemoteAccessTier.HIGH));
        REMOTE_ACCESS_ITEM_EXTREME = register("remote_access_item_extreme", () -> new RemoteAccessItem(RemoteAccessTier.EXTREME));
        REMOTE_CORE = register("remote_core", () -> new Item(new Item.Settings()));
    }
}
