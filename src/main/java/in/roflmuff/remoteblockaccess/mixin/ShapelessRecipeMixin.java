package in.roflmuff.remoteblockaccess.mixin;

import net.minecraft.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeMixin {

    @Accessor("group")
    String getGroupRBA();

}