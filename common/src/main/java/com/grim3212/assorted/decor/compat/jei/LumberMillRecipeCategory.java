package com.grim3212.assorted.decor.compat.jei;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.crafting.LumberMillRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.ItemStack;

/** Laid out as JEI's own stonecutter page: the planks, an arrow, what they are cut into. */
public class LumberMillRecipeCategory extends AbstractRecipeCategory<LumberMillRecipe> {

    public LumberMillRecipeCategory(IGuiHelper guiHelper, IRecipeType<LumberMillRecipe> type) {
        super(type, DecorBlocks.LUMBER_MILL.get().getName(), guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(DecorBlocks.LUMBER_MILL.get())), 82, 34);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LumberMillRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 9).setStandardSlotBackground().add(recipe.input());
        builder.addOutputSlot(61, 9).setOutputSlotBackground().add(recipe.resultItem());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, LumberMillRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrowWidget().setPosition(26, 9);
    }
}
