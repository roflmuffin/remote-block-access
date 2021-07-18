package in.roflmuff.remoteblockaccess.recipes;

import in.roflmuff.remoteblockaccess.RemoteBlockAccess;
import in.roflmuff.remoteblockaccess.items.RemoteAccessItem;
import in.roflmuff.remoteblockaccess.mixin.ShapelessRecipeMixin;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RechargeAccessItemRecipe extends ShapelessRecipe {

    public RechargeAccessItemRecipe(ShapelessRecipe original) {
        super(original.getId(), ((ShapelessRecipeMixin) original).getGroupRBA(), original.getOutput(),
                original.getIngredients());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RechargeAccessItemRecipeSerializer.INSTANCE;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = this.getOutput().copy();

        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        int numOfPearls = 0;
        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            if (item == Items.ENDER_PEARL) numOfPearls += stack.getCount();
        }

        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            if (item instanceof RemoteAccessItem) {
                output = stack.copy();
                int newDamage = Math.max(0, stack.getDamage() - (numOfPearls * 16));
                output.setDamage(newDamage);
            }
        }

        return output;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();
            if (item == Items.ENDER_PEARL) {
                stack.setCount(0);
            }
        }

        return defaultedList;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean standardMatch = super.matches(inventory, world);
        if (standardMatch) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

            for (int i = 0; i < defaultedList.size(); ++i) {
                ItemStack stack = inventory.getStack(i);
                Item item = stack.getItem();
                if (item instanceof RemoteAccessItem) {
                    if (stack.getDamage() > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
