package com.grim3212.assorted.buildingblocks.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.api.BuildingBlocksTags;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.items.BuildingBlocksItems;
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
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BuildingBlocksRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public BuildingBlocksRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Extra conditions for tag availability
        this.addConditions(itemTagExists(BuildingBlocksTags.Items.INGOTS_STEEL), BuildingBlocksBlocks.STEEL_DOOR.getId(), prefix("chain_link_steel"));
        this.addConditions(itemTagExists(BuildingBlocksTags.Items.INGOTS_ALUMINUM), prefix("chain_link_aluminum"));
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, BuildingBlocksBlocks.DECORATIVE_STONE.get(), 1).unlockedBy("has_stone", has(Blocks.STONE)).save(this.output, key("decorative_path_stonecutting"));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksItems.CHAIN_LINK.get(), 4).define('X', LibCommonTags.Items.INGOTS_IRON).define('N', LibCommonTags.Items.NUGGETS_IRON).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksBlocks.CHAIN_LINK_FENCE.get(), 8).define('X', BuildingBlocksItems.CHAIN_LINK.get()).pattern("XXX").pattern("XXX").unlockedBy("has_chain_link", has(BuildingBlocksItems.CHAIN_LINK.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksBlocks.QUARTZ_DOOR.get(), 3).define('X', Items.QUARTZ).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_quartz", has(Items.QUARTZ)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksBlocks.GLASS_DOOR.get(), 3).define('X', LibCommonTags.Items.GLASS).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_glass", has(LibCommonTags.Items.GLASS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksBlocks.CHAIN_LINK_DOOR.get(), 3).define('X', BuildingBlocksItems.CHAIN_LINK.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_chain_link", has(BuildingBlocksItems.CHAIN_LINK.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksBlocks.STEEL_DOOR.get(), 3).define('X', BuildingBlocksTags.Items.INGOTS_STEEL).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_steel", has(BuildingBlocksTags.Items.INGOTS_STEEL)).save(this.output, key(BuildingBlocksBlocks.STEEL_DOOR.getId().getPath()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksItems.CHAIN_LINK.get(), 4).define('X', BuildingBlocksTags.Items.INGOTS_STEEL).define('N', BuildingBlocksTags.Items.NUGGETS_STEEL).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(BuildingBlocksTags.Items.INGOTS_STEEL)).save(this.output, key("chain_link_steel"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, BuildingBlocksItems.CHAIN_LINK.get(), 4).define('X', BuildingBlocksTags.Items.INGOTS_ALUMINUM).define('N', BuildingBlocksTags.Items.NUGGETS_ALUMINUM).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(BuildingBlocksTags.Items.INGOTS_ALUMINUM)).save(this.output, key("chain_link_aluminum"));

        new BuildingBlockRecipes(this.items, this.output).build();
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
            return new BuildingBlocksRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
