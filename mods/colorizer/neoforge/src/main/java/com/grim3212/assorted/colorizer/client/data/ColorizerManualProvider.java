package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.Family;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * This part's chapters of the Assorted Decor section, which every part shares; the explicit chapter
 * orders keep the section's order whichever parts are installed.
 */
public class ColorizerManualProvider extends LibManualProvider {

    public ColorizerManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        this.addColorizer();
        this.addFurniture();
        this.addFires();
    }

    private void addColorizer() {
        Block[] shapes = {ColorizerBlocks.COLORIZER_SLAB.get(), ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(),
                ColorizerBlocks.COLORIZER_STAIRS.get(), ColorizerBlocks.COLORIZER_WALL.get(),
                ColorizerBlocks.COLORIZER_FENCE.get(), ColorizerBlocks.COLORIZER_FENCE_GATE.get()};
        Block[] slopes = {ColorizerBlocks.COLORIZER_SLOPE.get(), ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get(),
                ColorizerBlocks.COLORIZER_SLANTED_CORNER.get(), ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get(),
                ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get(), ColorizerBlocks.COLORIZER_SLOPED_POST.get(),
                ColorizerBlocks.COLORIZER_CORNER.get()};
        Block[] pyramids = {ColorizerBlocks.COLORIZER_PYRAMID.get(), ColorizerBlocks.COLORIZER_FULL_PYRAMID.get()};
        Block[] doors = {ColorizerBlocks.COLORIZER_DOOR.get(), ColorizerBlocks.COLORIZER_TRAP_DOOR.get()};

        ChapterBuilder colorizer = this.chapter("colorizer", 1);
        colorizer.recipes("colorizer", ColorizerBlocks.COLORIZER.get()).opens(ColorizerBlocks.COLORIZER.get());
        colorizer.recipes("brush", ColorizerItems.COLORIZER_BRUSH.get()).opens(ColorizerItems.COLORIZER_BRUSH.get());
        colorizer.recipes("shapes", ColorizerBlocks.COLORIZER_SLAB.get(), ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(), ColorizerBlocks.COLORIZER_STAIRS.get(), ColorizerBlocks.COLORIZER_WALL.get(), ColorizerBlocks.COLORIZER_FENCE.get(), ColorizerBlocks.COLORIZER_FENCE_GATE.get()).every(50).opens(shapes);
        colorizer.recipes("slopes", ColorizerBlocks.COLORIZER_SLOPE.get(), ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get(), ColorizerBlocks.COLORIZER_SLANTED_CORNER.get(), ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get(), ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get(), ColorizerBlocks.COLORIZER_SLOPED_POST.get(), ColorizerBlocks.COLORIZER_CORNER.get()).every(50).opens(slopes);
        colorizer.recipes("pyramids", ColorizerBlocks.COLORIZER_PYRAMID.get(), ColorizerBlocks.COLORIZER_FULL_PYRAMID.get()).every(50).opens(pyramids);
        colorizer.recipes("doors", ColorizerBlocks.COLORIZER_DOOR.get(), ColorizerBlocks.COLORIZER_TRAP_DOOR.get()).every(50).opens(doors);
        colorizer.recipesById("building", recipeId("colorizer_panel_stonecutting"), recipeId("colorizer_beam_stonecutting"), recipeId("colorizer_column_stonecutting")).every(50)
                .opens(ColorizerBlocks.COLORIZER_PANEL.get(), ColorizerBlocks.COLORIZER_BEAM.get(), ColorizerBlocks.COLORIZER_COLUMN.get());
    }

    private void addFurniture() {
        Block[] tables = {ColorizerBlocks.COLORIZER_TABLE.get(), ColorizerBlocks.COLORIZER_COUNTER.get()};
        Block[] seats = {ColorizerBlocks.COLORIZER_CHAIR.get(), ColorizerBlocks.COLORIZER_STOOL.get()};

        ChapterBuilder furniture = this.chapter("furniture", 2);
        furniture.recipes("tables", ColorizerBlocks.COLORIZER_TABLE.get(), ColorizerBlocks.COLORIZER_COUNTER.get()).every(50).opens(tables);
        furniture.recipes("seats", ColorizerBlocks.COLORIZER_CHAIR.get(), ColorizerBlocks.COLORIZER_STOOL.get()).every(50).opens(seats);
        furniture.recipes("lamp_post", ColorizerBlocks.COLORIZER_LAMP_POST.get()).opens(ColorizerBlocks.COLORIZER_LAMP_POST.get());
    }

    private void addFires() {
        Block[] firepits = {ColorizerBlocks.COLORIZER_FIREPIT.get(), ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get()};

        ChapterBuilder fires = this.chapter("fires", 3);
        fires.recipes("fireplace", ColorizerBlocks.COLORIZER_FIREPLACE.get()).opens(ColorizerBlocks.COLORIZER_FIREPLACE.get());
        fires.recipes("stove", ColorizerBlocks.COLORIZER_STOVE.get()).opens(ColorizerBlocks.COLORIZER_STOVE.get());
        fires.recipes("firepit", ColorizerBlocks.COLORIZER_FIREPIT.get(), ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get()).every(50).opens(firepits);
        fires.recipes("firering", ColorizerBlocks.COLORIZER_FIRERING.get()).opens(ColorizerBlocks.COLORIZER_FIRERING.get());
        fires.recipes("chimney", ColorizerBlocks.COLORIZER_CHIMNEY.get()).opens(ColorizerBlocks.COLORIZER_CHIMNEY.get());
    }
}
