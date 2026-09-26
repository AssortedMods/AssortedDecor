package com.grim3212.assorted.displays.client.data;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.displays.Family;
import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.items.DisplaysItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * This part's chapter of the Assorted Decor section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every block and item has to open a page, or the provider refuses to generate.
 */
public class DisplaysManualProvider extends LibManualProvider {

    public DisplaysManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder displays = this.chapter("displays", 10);
        displays.recipes("cage", DisplaysBlocks.CAGE.get()).opens(DisplaysBlocks.CAGE.get());

        // Every case but the museum one, whose page is below. The copper eight open on the copper
        // page rather than here, so no block is opened by two pages.
        Block[] displayCases = {DisplaysBlocks.WOODEN_DISPLAY_CASE.get(), DisplaysBlocks.STONE_DISPLAY_CASE.get(),
                DisplaysBlocks.IRON_DISPLAY_CASE.get(), DisplaysBlocks.GOLD_DISPLAY_CASE.get(), DisplaysBlocks.DIAMOND_DISPLAY_CASE.get()};
        Block copper = DisplaysBlocks.COPPER_DISPLAY_CASES.weathering().unaffected().get();
        displays.recipes("display_cases", DisplaysBlocks.WOODEN_DISPLAY_CASE.get(), DisplaysBlocks.STONE_DISPLAY_CASE.get(), copper, DisplaysBlocks.IRON_DISPLAY_CASE.get(), DisplaysBlocks.GOLD_DISPLAY_CASE.get(), DisplaysBlocks.DIAMOND_DISPLAY_CASE.get()).every(50)
                .opens(displayCases);
        displays.recipes("resizing_tool", DisplaysItems.RESIZING_TOOL.get()).opens(DisplaysItems.RESIZING_TOOL.get());
        displays.recipes("copper_display_cases", DisplaysBlocks.COPPER_DISPLAY_CASES.waxed().unaffected().get()).every(50)
                .opens(DisplaysBlocks.COPPER_DISPLAY_CASES.asList().stream().map(IRegistryObject::get).toArray(Block[]::new));
        displays.recipes("museum_display_case", DisplaysBlocks.MUSEUM_DISPLAY_CASE.get()).opens(DisplaysBlocks.MUSEUM_DISPLAY_CASE.get());
    }
}
