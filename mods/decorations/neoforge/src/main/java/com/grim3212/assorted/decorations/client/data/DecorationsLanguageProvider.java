package com.grim3212.assorted.decorations.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.decorations.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class DecorationsLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public DecorationsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.addManual();
    }

    /** This part's chapter of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.decorations", "Decorations");

        this.add("manual.assorteddecor.chapter.decorations.clay.title", "Clay Decorations");
        this.add("manual.assorteddecor.chapter.decorations.clay",
                "Clay decorations are crafted unfired and have to go through a furnace before "
                        + "they are any use." + BREAK
                        + "Once placed, right click to cycle through the options.");

        this.add("manual.assorteddecor.chapter.decorations.planter_pot.title", "Planter Pots");
        this.add("manual.assorteddecor.chapter.decorations.planter_pot",
                "A planter pot is crafted unfired and has to go through a furnace before it will hold "
                        + "anything." + BREAK
                        + "Once placed, right click to cycle through the filling material. The pot supports whichever "
                        + "plants suit the filling material it is set to.");

        this.add("manual.assorteddecor.chapter.decorations.bone.title", "Bone Decorations");
        this.add("manual.assorteddecor.chapter.decorations.bone",
                "A bone decoration needs no firing. Place it and right click to cycle through the shapes.");

        this.add("manual.assorteddecor.chapter.decorations.fountain.title", "Fountain");
        this.add("manual.assorteddecor.chapter.decorations.fountain",
                "A fountain block pushes water up out of itself, for a courtyard that needed a middle.");
    }
}
