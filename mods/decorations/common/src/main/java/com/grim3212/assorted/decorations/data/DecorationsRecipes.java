package com.grim3212.assorted.decorations.data;

import com.grim3212.assorted.decorations.Constants;
import com.grim3212.assorted.decorations.api.DecorationsTags;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.items.DecorationsItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class DecorationsRecipes extends ConditionalRecipeProvider {

    public DecorationsRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        this.addConditions(itemTagExists(DecorationsTags.Items.INGOTS_STEEL), prefix("fountain_steel"));
        this.addConditions(itemTagExists(DecorationsTags.Items.INGOTS_ALUMINUM), prefix("fountain_aluminum"));
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsItems.UNFIRED_PLANTER_POT.get()).define('X', Items.CLAY_BALL).pattern("X X").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(this.output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorationsItems.UNFIRED_PLANTER_POT.get()), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, DecorationsBlocks.PLANTER_POT.get(), 0.35f, 200).unlockedBy("has_unfired_planter_pot", has(DecorationsItems.UNFIRED_PLANTER_POT.get())).save(this.output);

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsItems.UNFIRED_CLAY_DECORATION.get()).define('X', Items.CLAY_BALL).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsBlocks.BONE_DECORATION.get()).define('X', LibCommonTags.Items.BONES).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_bones", has(LibCommonTags.Items.BONES)).save(this.output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorationsItems.UNFIRED_CLAY_DECORATION.get()), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, DecorationsBlocks.CLAY_DECORATION.get(), 0.35f, 200).unlockedBy("has_unfired_clay_decoration", has(DecorationsItems.UNFIRED_CLAY_DECORATION.get())).save(this.output);

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorationsTags.Items.INGOTS_ALUMINUM).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorationsTags.Items.INGOTS_ALUMINUM)).save(this.output, key("fountain_aluminum"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorationsTags.Items.INGOTS_STEEL).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorationsTags.Items.INGOTS_STEEL)).save(this.output, key("fountain_steel"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorationsBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
    }
}
