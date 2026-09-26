package com.grim3212.assorted.roads.data;

import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.roads.api.RoadsTags;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.items.RoadsItems;
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
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class RoadsRecipes extends ConditionalRecipeProvider {

    private static final String LIGHTS_MOD_ID = "assortedlights";
    // Assorted Lights' plate, named by its tag so this mod needs none of its code.
    private static final TagKey<Item> ILLUMINATION_PLATES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LIGHTS_MOD_ID, "illumination_plates"));

    private final HolderGetter<Item> items;

    public RoadsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Extra conditions for tag availability
        this.addConditions(itemTagExists(RoadsTags.Items.INGOTS_STEEL), prefix("steel_roadway_manhole"));
        this.addConditions(modLoaded(LIGHTS_MOD_ID), RoadsBlocks.ROADWAY_LIGHT.getId());
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // Stonecut, not crafted: six stones in two rows are already a wall for most of #c:stones.
        SingleItemRecipeBuilder.stonecutting(this.tag(LibCommonTags.Items.STONE), RecipeCategory.DECORATIONS, RoadsBlocks.SIDEWALK.get(), 1).unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).save(this.output, key(RoadsBlocks.SIDEWALK.getId()));
        SimpleCookingRecipeBuilder.smelting(this.tag(RoadsTags.Items.TAR), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, RoadsItems.ASPHALT.get(), 0.35f, 200).unlockedBy("has_tar", has(RoadsTags.Items.TAR)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, RoadsBlocks.ROADWAY.get()).define('A', RoadsItems.ASPHALT.get()).define('X', LibCommonTags.Items.STONE).pattern("A").pattern("X").unlockedBy("has_asphalt", has(RoadsItems.ASPHALT.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, RoadsBlocks.ROADWAY_MANHOLE.get()).define('M', LibCommonTags.Items.INGOTS_IRON).define('X', RoadsBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(RoadsBlocks.ROADWAY.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, RoadsBlocks.ROADWAY_LIGHT.get()).define('M', ILLUMINATION_PLATES).define('X', RoadsBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(RoadsBlocks.ROADWAY.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, RoadsBlocks.ROADWAY_MANHOLE.get()).define('M', RoadsTags.Items.INGOTS_STEEL).define('X', RoadsBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(RoadsBlocks.ROADWAY.get())).save(this.output, key("steel_roadway_manhole"));
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, RoadsBlocks.ROADWAY.get()).requires(RoadsTags.Items.ROADWAYS_COLOR).requires(fluid(FluidTags.WATER)).unlockedBy("has_roadway_color", has(RoadsTags.Items.ROADWAYS_COLOR)).save(this.output, key(RoadsBlocks.ROADWAY.getId().getPath() + "_wash"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, RoadsItems.TARBALL.get(), 16).define('X', ItemTags.COALS).define('G', LibCommonTags.Items.GRAVEL).define('W', fluid(FluidTags.WATER)).pattern("X").pattern("G").pattern("W").unlockedBy("has_coal", has(ItemTags.COALS)).save(this.output);

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, RoadsBlocks.STONE_PATH.get(), 1).unlockedBy("has_stone", has(Blocks.STONE)).save(this.output, key("stone_path_stonecutting"));
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
    }

    private static ResourceKey<Recipe<?>> key(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
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
            return new RoadsRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
