package in.roflmuff.remoteblockaccess.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;


public class RechargeAccessItemRecipeSerializer extends ShapelessRecipe.Serializer {

    public static final RechargeAccessItemRecipeSerializer INSTANCE = new RechargeAccessItemRecipeSerializer();

    @Override
    public ShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
        return new RechargeAccessItemRecipe(super.read(identifier, jsonObject));
    }

    @Override
    public ShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
        return new RechargeAccessItemRecipe(super.read(identifier, packetByteBuf));
    }

}
