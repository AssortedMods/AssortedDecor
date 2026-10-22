package com.grim3212.assorted.gates.data;

import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Recipe;

public class GatesRecipes extends ConditionalRecipeProvider {

    public GatesRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
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
}
