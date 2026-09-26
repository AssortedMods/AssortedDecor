package com.grim3212.assorted.buildingblocks.common.crafting;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.Family;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class BuildingBlocksRecipeTypes {

    // Aliased so datapacks that still write assorteddecor:lumber_mill recipes keep loading.
    public static final RegistryProvider<RecipeType<?>> RECIPE_TYPES = RegistryProvider.create(Registries.RECIPE_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);
    public static final RegistryProvider<RecipeSerializer<?>> RECIPE_SERIALIZERS = RegistryProvider.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<RecipeType<LumberMillRecipe>> LUMBER_MILL = RECIPE_TYPES.register("lumber_mill", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Constants.MOD_ID + ":lumber_mill";
        }
    });

    static {
        RECIPE_SERIALIZERS.register("lumber_mill", () -> LumberMillRecipe.SERIALIZER);
    }

    private BuildingBlocksRecipeTypes() {
    }

    /** The lumber mill lists its recipes on the client, where 26.2 keeps no recipe manager. */
    public static void init() {
        SyncedRecipes.require(LUMBER_MILL::get, LumberMillRecipe.SERIALIZER);
    }
}
