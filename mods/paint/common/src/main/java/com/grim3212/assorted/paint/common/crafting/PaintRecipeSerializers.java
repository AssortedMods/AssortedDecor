package com.grim3212.assorted.paint.common.crafting;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.paint.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PaintRecipeSerializers {

    public static final RegistryProvider<RecipeSerializer<?>> RECIPES = RegistryProvider.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final IRegistryObject<RecipeSerializer<RollerDyeingRecipe>> ROLLER_DYEING = RECIPES.register("roller_dyeing", () -> RollerDyeingRecipe.SERIALIZER);

    public static void init() {
    }
}
