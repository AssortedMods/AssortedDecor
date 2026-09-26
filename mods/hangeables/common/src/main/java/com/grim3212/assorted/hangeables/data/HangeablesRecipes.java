package com.grim3212.assorted.hangeables.data;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class HangeablesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public HangeablesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesItems.WALLPAPER.get()).define('X', ItemTags.WOOL).define('#', Items.PAPER).pattern("#X").pattern("#X").pattern("#X").unlockedBy("has_paper", has(Items.PAPER)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesItems.WOOD_FRAME.get()).define('X', ItemTags.PLANKS).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesItems.IRON_FRAME.get()).define('X', LibCommonTags.Items.INGOTS_IRON).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesBlocks.CALENDAR.get()).define('#', Items.PAPER).pattern("##").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('C', Items.CLOCK).pattern("###").pattern("#C#").pattern("###").unlockedBy("has_clock", has(Items.CLOCK)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('G', LibCommonTags.Items.INGOTS_GOLD).pattern("#G#").pattern("GRG").pattern("#G#").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).unlockedBy("has_gold", has(LibCommonTags.Items.INGOTS_GOLD)).save(this.output, key("wall_clock_alt"));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, HangeablesItems.NEON_SIGN.get(), 3).define('X', LibCommonTags.Items.OBSIDIAN).define('G', ItemTags.PLANKS).define('C', LibCommonTags.Items.DUSTS_REDSTONE).pattern("XXX").pattern("XCX").pattern(" G ").unlockedBy("has_obsidian", has(LibCommonTags.Items.OBSIDIAN)).save(this.output);
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
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
            return new HangeablesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
