package com.grim3212.assorted.paint.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.paint.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. An item whose name is its id in title case needs no line here
 * (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class PaintLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public PaintLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("tag.item.assortedpaint.paint_rollers", "Paint Rollers");

        this.nameItems("paint_roller_" + dyeColors(), m -> titleCase(m.group(1)) + " Paint Roller");

        this.addManual();
    }

    /** This part's chapter of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.paint", "Paint");

        this.add("manual.assorteddecor.chapter.paint.rollers.title", "Paint Rollers");
        this.add("manual.assorteddecor.chapter.paint.rollers",
                "A paint roller loaded with a dye is how road markings and siding get their color. Use it on wool, "
                        + "carpet, concrete or concrete powder to recolor it as well." + BREAK
                        + "There is a roller for every color.");

        this.add("manual.assorteddecor.chapter.paint.roadways.title", "Road Markings");
        this.add("manual.assorteddecor.chapter.paint.roadways",
                "Craft a roadway with a roller to paint it, or use the roller on a roadway that is already down." + BREAK
                        + "Use a white roller on white roadway to switch between the different line markings.");

        this.add("manual.assorteddecor.chapter.paint.fluro.title", "Fluro Blocks");
        this.add("manual.assorteddecor.chapter.paint.fluro",
                "A fluro block of any color can be crafted with a roller to turn it into another color.");

        this.add("manual.assorteddecor.chapter.paint.siding.title", "Siding");
        this.add("manual.assorteddecor.chapter.paint.siding",
                "Siding boards a wall in horizontal or vertical planking, in any of the sixteen colors. The "
                        + "color comes from the paint roller in the recipe.");
    }
}
