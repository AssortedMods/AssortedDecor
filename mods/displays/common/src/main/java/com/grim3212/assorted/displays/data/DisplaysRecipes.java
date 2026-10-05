package com.grim3212.assorted.displays.data;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.items.DisplaysItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class DisplaysRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public DisplaysRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
    }

    /** Eight cases from eight glass and one of whatever the frame is made of, as in 1.4.6. */
    private void displayCase(Block result, TagKey<Item> frame, String unlockName) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, result, 1).define('G', LibCommonTags.Items.GLASS).define('F', frame).pattern("GGG").pattern("G G").pattern("GFG").unlockedBy(unlockName, has(frame)).save(this.output);
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // Cage
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DisplaysBlocks.CAGE.get(), 1).define('X', Items.IRON_BARS).pattern("XXX").pattern("X X").pattern("XXX").unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);

        // Display Cases
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DisplaysItems.RESIZING_TOOL.get()).define('I', LibCommonTags.Items.INGOTS_IRON).define('G', LibCommonTags.Items.NUGGETS_GOLD).define('S', LibCommonTags.Items.RODS_WOODEN)
                .pattern("III").pattern("IG ").pattern("S  ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        displayCase(DisplaysBlocks.WOODEN_DISPLAY_CASE.get(), ItemTags.PLANKS, "has_planks");
        displayCase(DisplaysBlocks.STONE_DISPLAY_CASE.get(), LibCommonTags.Items.STONE, "has_stone");
        displayCase(DisplaysBlocks.COPPER_DISPLAY_CASES.weathering().unaffected().get(), LibCommonTags.Items.INGOTS_COPPER, "has_copper");
        // Only waxing is craftable; the other three stages are weathered into, not made.
        DisplaysBlocks.COPPER_DISPLAY_CASES.zipUnwaxedWaxed((unwaxed, waxed) ->
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, waxed.get()).requires(unwaxed.get()).requires(Items.HONEYCOMB)
                        .group("waxed_copper_display_case").unlockedBy("has_honeycomb", has(Items.HONEYCOMB)).save(this.output));
        displayCase(DisplaysBlocks.IRON_DISPLAY_CASE.get(), LibCommonTags.Items.INGOTS_IRON, "has_iron");
        displayCase(DisplaysBlocks.GOLD_DISPLAY_CASE.get(), LibCommonTags.Items.INGOTS_GOLD, "has_gold");
        displayCase(DisplaysBlocks.DIAMOND_DISPLAY_CASE.get(), LibCommonTags.Items.GEMS_DIAMOND, "has_diamond");
        // The museum case keeps the sign the 1.4.6 recipe asked for, as the plinth's placard.
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DisplaysBlocks.MUSEUM_DISPLAY_CASE.get()).define('G', LibCommonTags.Items.GLASS).define('I', LibCommonTags.Items.INGOTS_GOLD).define('S', LibCommonTags.Items.STONE).define('P', ItemTags.SIGNS).pattern("GGG").pattern("SIS").pattern("SPS").unlockedBy("has_sign", has(ItemTags.SIGNS)).save(this.output);
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
            return new DisplaysRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
