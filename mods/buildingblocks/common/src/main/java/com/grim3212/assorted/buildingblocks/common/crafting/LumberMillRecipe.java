package com.grim3212.assorted.buildingblocks.common.crafting;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;

import java.util.List;

/** A stonecutter recipe for the lumber mill: one input, one result, picked from a list. */
public class LumberMillRecipe extends SingleItemRecipe {

    public static final MapCodec<LumberMillRecipe> MAP_CODEC = simpleMapCodec(LumberMillRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumberMillRecipe> STREAM_CODEC = simpleStreamCodec(LumberMillRecipe::new);
    public static final RecipeSerializer<LumberMillRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public LumberMillRecipe(Recipe.CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result) {
        super(commonInfo, ingredient, result);
    }

    /** What one cut makes, for the lumber mill's buttons. */
    public ItemStack resultItem() {
        return this.result().create();
    }

    @Override
    public RecipeType<LumberMillRecipe> getType() {
        return BuildingBlocksRecipeTypes.LUMBER_MILL.get();
    }

    @Override
    public RecipeSerializer<LumberMillRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new StonecutterRecipeDisplay(this.input().display(), new SlotDisplay.ItemStackSlotDisplay(this.result()), new SlotDisplay.ItemSlotDisplay(BuildingBlocksBlocks.LUMBER_MILL.get().asItem())));
    }

    // It has no recipe book of its own, any more than the stonecutter does.
    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
