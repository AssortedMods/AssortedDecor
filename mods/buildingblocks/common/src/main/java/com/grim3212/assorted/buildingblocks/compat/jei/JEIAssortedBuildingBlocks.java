package com.grim3212.assorted.buildingblocks.compat.jei;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.crafting.BuildingBlocksRecipeTypes;
import com.grim3212.assorted.buildingblocks.common.crafting.LumberMillRecipe;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The lumber mill's recipes in JEI, read from whatever the server sent, as the stonecutter's are:
 * a datapack's own lumber mill recipes show up with the mod's. The stonecutter recipes need nothing here.
 */
@JeiPlugin
public class JEIAssortedBuildingBlocks implements IModPlugin {

    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "jei_plugin");
    public static final IRecipeType<LumberMillRecipe> LUMBER_MILL = IRecipeType.create(Constants.MOD_ID, "lumber_mill", LumberMillRecipe.class);

    /** What JEI has been handed, so a later sync knows what to take back out. Client thread only. */
    private static List<LumberMillRecipe> shown = List.of();
    private static RecipeMap shownFrom = RecipeMap.EMPTY;
    private static @Nullable IJeiRuntime runtime;

    static {
        SyncedRecipes.addUpdateListener(JEIAssortedBuildingBlocks::onRecipesUpdated);
    }

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new LumberMillRecipeCategory(registration.getJeiHelpers().getGuiHelper(), LUMBER_MILL));
    }

    // The recipes come with the login packet, often after this runs; onRecipesUpdated catches those.
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        shownFrom = SyncedRecipes.recipes();
        shown = recipes();
        registration.addRecipes(LUMBER_MILL, shown);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(LUMBER_MILL, BuildingBlocksBlocks.LUMBER_MILL.get());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        onRecipesUpdated();
    }

    /** JEI's runtime once it has loaded, for the client gametest to look the recipes up in. */
    public static @Nullable IJeiRuntime runtime() {
        return runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    /** Swaps in a newly synced set, after login or a {@code /reload}; each sync is a new map, so identity spots it. */
    private static void onRecipesUpdated() {
        IJeiRuntime jeiRuntime = runtime;
        if (jeiRuntime == null || SyncedRecipes.recipes() == shownFrom) {
            return;
        }
        shownFrom = SyncedRecipes.recipes();
        jeiRuntime.getRecipeManager().hideRecipes(LUMBER_MILL, shown);
        shown = recipes();
        jeiRuntime.getRecipeManager().addRecipes(LUMBER_MILL, shown);
    }

    private static List<LumberMillRecipe> recipes() {
        return SyncedRecipes.byType(BuildingBlocksRecipeTypes.LUMBER_MILL.get()).stream().map(RecipeHolder::value).toList();
    }
}
