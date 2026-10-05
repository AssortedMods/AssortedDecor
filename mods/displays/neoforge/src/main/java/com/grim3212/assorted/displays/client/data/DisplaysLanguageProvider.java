package com.grim3212.assorted.displays.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.displays.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line here
 * (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class DisplaysLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public DisplaysLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("assorteddisplays.container.cage", "Cage");

        this.add("tooltip.resizing_tool", "Use on a display case to adjust the display size");

        this.addManual();
    }

    /** This part's chapter of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.displays", "Displays");

        this.add("manual.assorteddecor.chapter.displays.cage.title", "Cage");
        this.add("manual.assorteddecor.chapter.displays.cage",
                "A cage displays whatever you put in it, turning slowly. Use it with a Pokeball from Assorted Tools to show the caught monster.");
        this.add("manual.assorteddecor.chapter.displays.display_cases.title", "Display Cases");
        this.add("manual.assorteddecor.chapter.displays.display_cases",
                "A display case can hold 1, 4, or 9 items depending on the size setup in the display."
                        + BREAK + "Right clicking on a position allows you to place an item in that spot. Use the Resizing Tool to change the size of the case."
                        + BREAK + "Wooden, stone, copper, iron, gold and diamond cases differ only in the colour of their frame.");
        this.add("manual.assorteddecor.chapter.displays.resizing_tool.title", "Resizing Tool");
        this.add("manual.assorteddecor.chapter.displays.resizing_tool",
                "A case is placed showing one item on one shelf, and that one item is drawn large. Use the resizing tool on it to grow it to four items on two shelves, again for nine on three, and once more to come back to one."
                        + BREAK + "Shrinking a case hands back whatever it can no longer show.");
        this.add("manual.assorteddecor.chapter.displays.copper_display_cases.title", "Copper Display Cases");
        this.add("manual.assorteddecor.chapter.displays.copper_display_cases",
                "A copper case weathers where it stands, through exposed, weathered and oxidized, like any other copper. What is on show stays on show through every step."
                        + BREAK + "An axe scrapes a stage back off it, and a honeycomb waxes it to hold the stage it is at. Only the plain copper case is crafted.");
        this.add("manual.assorteddecor.chapter.displays.museum_display_case.title", "Museum Display Case");
        this.add("manual.assorteddecor.chapter.displays.museum_display_case",
                "The museum edition stands two blocks tall. A carpeted plinth with the glass case on top of it. Everything goes in the glass half."
                        + BREAK + "The plinth carries a placard. Name the case on an anvil before you place it, or right click the plinth with a named name tag.");
    }
}
