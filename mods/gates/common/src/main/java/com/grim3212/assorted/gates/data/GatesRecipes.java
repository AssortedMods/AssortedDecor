package com.grim3212.assorted.gates.data;

import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class GatesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public GatesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, GatesItems.GATE_GRATING.get(), 4).define('I', LibCommonTags.Items.INGOTS_IRON).pattern(" I ").pattern("III").pattern(" I ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, GatesItems.GARAGE_PANEL.get(), 4).define('I', LibCommonTags.Items.INGOTS_IRON).define('G', LibCommonTags.Items.GLASS).pattern("III").pattern(" G ").pattern("III").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GatesBlocks.CASTLE_GATE.get()).define('G', GatesItems.GATE_GRATING.get()).pattern("G").pattern("G").pattern("G").unlockedBy("has_gate_grating", has(GatesItems.GATE_GRATING.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GatesBlocks.GARAGE_DOOR.get()).define('P', GatesItems.GARAGE_PANEL.get()).pattern("P").pattern("P").pattern("P").unlockedBy("has_garage_panel", has(GatesItems.GARAGE_PANEL.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, GatesItems.GATE_TRUMPET.get()).define('W', ItemTags.WOOL).define('G', LibCommonTags.Items.INGOTS_GOLD).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("G  ").pattern("WG ").pattern(" WI").unlockedBy("has_gate_grating", has(GatesItems.GATE_GRATING.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, GatesItems.GARAGE_REMOTE.get()).define('B', BlockItemTags.STONE_BUTTONS.item()).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("B").pattern("R").pattern("I").unlockedBy("has_garage_panel", has(GatesItems.GARAGE_PANEL.get())).save(this.output);
    }

    /**
     * Recipe providers are not data providers any more - a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new GatesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
