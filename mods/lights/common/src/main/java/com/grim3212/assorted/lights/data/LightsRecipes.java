package com.grim3212.assorted.lights.data;

import com.grim3212.assorted.lights.Constants;
import com.grim3212.assorted.lights.api.LightsTags;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class LightsRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public LightsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Extra conditions for tag availability
        this.addConditions(itemTagExists(LightsTags.Items.INGOTS_ALUMINUM), LightsBlocks.ILLUMINATION_PLATE.getId(), LightsBlocks.ILLUMINATION_TUBE.getId());
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', LibCommonTags.Items.INGOTS_IRON).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key("illumination_tube_iron"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', LightsBlocks.ILLUMINATION_TUBE.get()).define('A', LibCommonTags.Items.INGOTS_IRON).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key("illumination_plate_iron"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', LightsBlocks.ILLUMINATION_TUBE.get()).define('A', LightsTags.Items.INGOTS_ALUMINUM).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(LightsTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key(LightsBlocks.ILLUMINATION_PLATE.getId().getPath()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', LightsTags.Items.INGOTS_ALUMINUM).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(LightsTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key(LightsBlocks.ILLUMINATION_TUBE.getId().getPath()));
        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
            FluroBlock b = x.getValue().get();
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, b, 4).define('G', LibCommonTags.Items.GLASS).define('L', LightsBlocks.ILLUMINATION_TUBE.get()).define('A', DyeHelper.getDyeTag(b.getColor())).pattern("GAG").pattern("ALA").pattern("GAG").unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).unlockedBy("has_tube", has(LightsBlocks.ILLUMINATION_TUBE.get())).save(this.output);
        });

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.PAPER_LANTERN.get()).define('P', Items.PAPER).define('C', LightsTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(LightsTags.Items.LANTERN_SOURCE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.BONE_LANTERN.get()).define('P', LibCommonTags.Items.BONES).define('C', LightsTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(LightsTags.Items.LANTERN_SOURCE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, LightsBlocks.IRON_LANTERN.get()).define('P', LibCommonTags.Items.INGOTS_IRON).define('C', LightsTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(LightsTags.Items.LANTERN_SOURCE)).save(this.output);
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
            return new LightsRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
