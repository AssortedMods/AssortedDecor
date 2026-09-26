package com.grim3212.assorted.colorizer.data;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ColorizerRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public ColorizerRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, ColorizerItems.COLORIZER_BRUSH.get()).requires(fluid(FluidTags.WATER)).requires(ColorizerItems.COLORIZER_BRUSH.get()).unlockedBy("has_brush", has(ColorizerItems.COLORIZER_BRUSH.get())).save(this.output, key("clean_colorizer_brush"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerItems.COLORIZER_BRUSH.get()).define('X', ColorizerBlocks.COLORIZER.get()).define('R', LibCommonTags.Items.RODS_WOODEN).define('S', LibCommonTags.Items.STRING).pattern(" SX").pattern(" RS").pattern("R  ").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER.get(), 4).define('X', LibCommonTags.Items.STONE).define('R', LibCommonTags.Items.DYES_RED).define('G', LibCommonTags.Items.DYES_GREEN).define('B', LibCommonTags.Items.DYES_BLUE).define('D', LibCommonTags.Items.DYES).pattern("XRX").pattern("GDB").pattern("XDX").unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_CHAIR.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("X  ").pattern("XXX").pattern("X X").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_TABLE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_STOOL.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern("S S").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_COUNTER.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern(" S ").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FENCE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XSX").pattern("XSX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FENCE_GATE.get(), 2).define('X', ColorizerBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("SXS").pattern("SXS").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_WALL.get(), 6).define('X', ColorizerBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_STAIRS.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("X  ").pattern("XX ").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLAB.get(), 6).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(), 6).define('X', ColorizerBlocks.COLORIZER.get()).pattern("X").pattern("X").pattern("X").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_DOOR.get(), 3).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_TRAP_DOOR.get(), 2).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_LAMP_POST.get(), 2).define('X', ColorizerBlocks.COLORIZER.get()).define('G', Blocks.GLOWSTONE).pattern("XGX").pattern("XXX").pattern(" X ").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).unlockedBy("has_glowstone", has(Blocks.GLOWSTONE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("X ").pattern("XX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XX ").pattern("X X").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_POST.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("X ").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_CORNER.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("XXX").pattern("XX ").pattern("X  ").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLANTED_CORNER.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern("  X").pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_PYRAMID.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FULL_PYRAMID.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).pattern(" X ").pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_CHIMNEY.get(), 6).define('X', ColorizerBlocks.COLORIZER.get()).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("X X").pattern("X X").pattern("XIX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FIREPIT.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("III").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FIREPLACE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XXX").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FIRERING.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern(" X ").pattern("XPX").pattern(" X ").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_STOVE.get(), 4).define('X', ColorizerBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("XXX").pattern("IPI").pattern("XXX").unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLAB.get(), 2).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(), 2).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_vertical_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_STAIRS.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_stairs_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_WALL.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_walls_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_CHAIR.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_chair_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_TABLE.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_table_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPE.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_angle_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_intersection_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLOPED_POST.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_post_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_oblique_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_CORNER.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_SLANTED_CORNER.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_slanted_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_PYRAMID.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_pyramid_stonecutting"));
        // The building shapes come off the stonecutter only, as the building blocks they copy do.
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_PANEL.get(), 8).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_panel_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_BEAM.get(), 4).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_beam_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_COLUMN.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_column_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ColorizerBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, ColorizerBlocks.COLORIZER_FULL_PYRAMID.get(), 1).unlockedBy("has_colorizer", has(ColorizerBlocks.COLORIZER.get())).save(this.output, key("colorizer_full_pyramid_stonecutting"));
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
            return new ColorizerRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
