package com.grim3212.assorted.lights.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.lights.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); these are the names that read differently, and
 * every key that is not a name.
 */
public class LightsLanguageProvider extends LibLanguageProvider {

    public LightsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        // The family's shared tab, written the same by every part.
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("tag.item.assortedlights.fluro", "Fluro Blocks");
        this.add("tag.item.assortedlights.lantern_source", "Lantern Light Sources");
        this.add("tag.item.assortedlights.illumination_plates", "Illumination Plates");

        // Families whose names read differently from their ids.
        this.nameBlocks("fluro_" + dyeColors(), m -> titleCase(m.group(1)) + " Fluro Tube");

        this.addManual();
    }

    /** The chapters in {@code assets/assorteddecor/manual} name these keys. */
    private void addManual() {
        // The family's shared manual section, written the same by every part.
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.lights", "Lights");

        this.add("manual.assorteddecor.chapter.lights.fluro.title", "Fluro Blocks");
        this.add("manual.assorteddecor.chapter.lights.fluro",
                "Fluro blocks are bright, flat and come in all sixteen colors.");

        this.add("manual.assorteddecor.chapter.lights.illumination.title", "Illumination Tubes");
        this.add("manual.assorteddecor.chapter.lights.illumination",
                "Illumination tubes and plates light a room the way a torch does, but go on any face of a block and sit flush against it.");

        this.add("manual.assorteddecor.chapter.lights.lanterns.title", "Lanterns");
        this.add("manual.assorteddecor.chapter.lights.lanterns",
                "Bone, iron and paper lanterns, for when a torch is the wrong look for the room.");
    }
}
