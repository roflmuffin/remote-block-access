package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModItems {
    public static RemoteAccessItem REMOTE_ACCESS_ITEM;

    private static <T extends Item> T register(String name, final Supplier<T> sup) {
        return Registry.register(Registry.ITEM, new Identifier(RemoteBlockAccess.MOD_ID, name), sup.get());
    }

    public static void register() {
        REMOTE_ACCESS_ITEM = register("remote_access_item", RemoteAccessItem::new);
    }
}
