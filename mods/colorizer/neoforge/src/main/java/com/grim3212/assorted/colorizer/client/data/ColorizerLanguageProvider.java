package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line
 * here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class ColorizerLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public ColorizerLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("tooltip.colorizer_brush.empty", "Empty");
        this.add("tooltip.colorizer_brush.stored", "Stored: %s");

        this.add("block.assortedcolorizer.colorizer_trap_door", "Colorizer Trapdoor");
        this.add("block.assortedcolorizer.colorizer_firepit_covered", "Colorizer Covered Firepit");

        this.addManual();
    }

    /** This part's chapters of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.addColorizerChapter();
        this.addFurnitureChapter();
        this.addFiresChapter();
    }

    private void addColorizerChapter() {
        this.add("manual.assorteddecor.chapter.colorizer", "Colorizers");

        this.add("manual.assorteddecor.chapter.colorizer.colorizer.title", "Colorizers");
        this.add("manual.assorteddecor.chapter.colorizer.colorizer",
                "A colorizer is a blank block that borrows another block's texture." +BREAK
                        + "Everything in this chapter and the two after it is built from colorizers, which is "
                        + "why a chair, a slope and a fireplace can all match the wall behind them.");

        this.add("manual.assorteddecor.chapter.colorizer.brush.title", "Colorizer Brush");
        this.add("manual.assorteddecor.chapter.colorizer.brush",
                "A colorized block can only be set by being brushed with the Colorizer Brush." + BREAK
                        + "Shift right click a supported full block and the brush will take the blocks texture. Then right click with the brush on a Colorizer and see it get applied.");

        this.add("manual.assorteddecor.chapter.colorizer.shapes.title", "Basic Shapes");
        this.add("manual.assorteddecor.chapter.colorizer.shapes",
                "Slabs, vertical slabs, stairs, walls, fences and fence gates, all colorizable.");

        this.add("manual.assorteddecor.chapter.colorizer.slopes.title", "Slopes");
        this.add("manual.assorteddecor.chapter.colorizer.slopes",
                "Slopes are the angled half of the set. A slope is half a block cut corner to corner and walks "
                        + "like straight stairs; a sloped angle can only be walked up on the point it faces." + BREAK
                        + "Sloped intersections act as corner stairs, oblique slopes fill the corner a "
                        + "staircase leaves, slanted corners are a steeper climb, and sloped posts cannot be "
                        + "climbed at all. Corners are a sideways cut of a full block." + BREAK
                        + "All of them can be flipped upside down as you place them, the way a slab is.");

        this.add("manual.assorteddecor.chapter.colorizer.building.title", "Panels, Beams and Columns");
        this.add("manual.assorteddecor.chapter.colorizer.building",
                "The panel, beam and column from building blocks, cut from a colorizer on the stonecutter. They behave just as the wooden and stone ones do.");

        this.add("manual.assorteddecor.chapter.colorizer.pyramids.title", "Pyramids");
        this.add("manual.assorteddecor.chapter.colorizer.pyramids",
                "A pyramid takes up half a block, and a large pyramid takes a full one and can be climbed, "
                        + "with its high point in the middle.");

        this.add("manual.assorteddecor.chapter.colorizer.doors.title", "Doors and Trap Doors");
        this.add("manual.assorteddecor.chapter.colorizer.doors",
                "A colorized door wearing the same block as the wall around it is a hidden entrance, and a "
                        + "colorized trap door does the same for a floor.");
    }

    private void addFurnitureChapter() {
        this.add("manual.assorteddecor.chapter.furniture", "Furniture");

        this.add("manual.assorteddecor.chapter.furniture.tables.title", "Tables and Counters");
        this.add("manual.assorteddecor.chapter.furniture.tables",
                "Tables placed next to each other work out where their legs belong, so a long table has room "
                        + "underneath it." + BREAK
                        + "A counter is a table without the legs, for running along a wall.");

        this.add("manual.assorteddecor.chapter.furniture.seats.title", "Chairs and Stools");
        this.add("manual.assorteddecor.chapter.furniture.seats",
                "Chairs turn to face the way you place them. Stools are the shorter version, and a planter pot "
                        + "sits on stool nicely.");

        this.add("manual.assorteddecor.chapter.furniture.lamp_post.title", "Lamp Posts");
        this.add("manual.assorteddecor.chapter.furniture.lamp_post",
                "A lamp post builds itself three blocks high from the one you place. And the top gives light.");
    }

    private void addFiresChapter() {
        this.add("manual.assorteddecor.chapter.fires", "Fires");

        this.add("manual.assorteddecor.chapter.fires.fireplace.title", "Fireplace");
        this.add("manual.assorteddecor.chapter.fires.fireplace",
                "Light a fireplace with flint and steel and it burns and gives light. Punch it to put it out."
                        + BREAK
                        + "Fireplaces placed side by side move their corner posts out of each other's way, so a "
                        + "row reads as one wide hearth.");

        this.add("manual.assorteddecor.chapter.fires.stove.title", "Stove");
        this.add("manual.assorteddecor.chapter.fires.stove",
                "A stove lights and goes out the same way a fireplace does, and takes a chimney on top.");

        this.add("manual.assorteddecor.chapter.fires.firepit.title", "Firepits");
        this.add("manual.assorteddecor.chapter.fires.firepit",
                "A firepit is the outdoor version, lit with flint and steel and put out with a punch. The covered one has a net over it.");

        this.add("manual.assorteddecor.chapter.fires.firering.title", "Fire Ring");
        this.add("manual.assorteddecor.chapter.fires.firering",
                "A fire ring is a ring of stones around a fire, lit and put out like the rest. Good for ghost stories.");

        this.add("manual.assorteddecor.chapter.fires.chimney.title", "Chimney");
        this.add("manual.assorteddecor.chapter.fires.chimney",
                "A chimney on a lit fireplace smokes. They stack, so the smoke can be carried up through a roof.");
    }
}
