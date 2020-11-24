package in.roflmuff.remoteblockaccess.core;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.recipes.RechargeAccessItemRecipeSerializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModRecipes {
    public static void register() {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(RemoteBlockAccess.MOD_ID, "recharger"), RechargeAccessItemRecipeSerializer.INSTANCE);
    }
}
