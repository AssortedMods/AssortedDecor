package com.grim3212.assorted.roads.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.roads.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); these are the names that read differently, and
 * every key that is not a name.
 */
public class RoadsLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public RoadsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        // The family's shared tab, written the same by every part.
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("tag.item.assortedroads.roadways", "Roadways");
        this.add("tag.item.assortedroads.roadways.all", "All Roadways");
        this.add("tag.item.assortedroads.roadways.color", "Colored Roadways");
        this.add("tag.item.assortedroads.road_line_painters", "Road Line Painters");
        this.add("tag.item.c.tar", "Tar");

        // Families whose names read differently from their ids.
        this.nameBlocks("roadway_" + dyeColors(), m -> titleCase(m.group(1)) + " Roadway");

        this.addManual();
    }

    /** The chapters in {@code assets/assorteddecor/manual} name these keys. */
    private void addManual() {
        // The family's shared manual section, written the same by every part.
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.roads", "Roads");

        this.add("manual.assorteddecor.chapter.roads.roadway.title", "Roadway");
        this.add("manual.assorteddecor.chapter.roads.roadway",
                "Roadway is asphalt laid flat." + BREAK
                        + "It comes in all sixteen colors for markings, plus a lit version and a manhole, so a "
                        + "road can be striped like a real one.");

        this.add("manual.assorteddecor.chapter.roads.roadway_light.title", "Roadway Light");
        this.add("manual.assorteddecor.chapter.roads.roadway_light",
                "A roadway light is roadway with an illumination plate from Assorted Lights set into it. It lights up while redstone powers it.");

        this.add("manual.assorteddecor.chapter.roads.asphalt.title", "Asphalt and Tar");
        this.add("manual.assorteddecor.chapter.roads.asphalt",
                "Tarballs make asphalt, and asphalt is what every road block here is built from.");

        this.add("manual.assorteddecor.chapter.roads.sidewalk.title", "Sidewalks and Paths");
        this.add("manual.assorteddecor.chapter.roads.sidewalk",
                "Sidewalk allow you to walk faster than you otherwise would and paths just look nice. 🙂");

        this.add("manual.assorteddecor.chapter.roads.stone_path.title", "Stone Path");
        this.add("manual.assorteddecor.chapter.roads.stone_path",
                "A stone path is the quieter surface for a garden, cut from plain stone on the "
                        + "stonecutter.");
    }
}
