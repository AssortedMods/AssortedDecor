package com.grim3212.assorted.hangeables.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.hangeables.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class HangeablesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public HangeablesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("block.assortedhangeables.neon_sign_wall", "Neon Sign");

        this.add("screen.assortedhangeables.neon_sign.bold", "Bold");
        this.add("screen.assortedhangeables.neon_sign.italic", "Italic");
        this.add("screen.assortedhangeables.neon_sign.underline", "Underline");
        this.add("screen.assortedhangeables.neon_sign.strikethrough", "Strikethrough");
        this.add("screen.assortedhangeables.neon_sign.random", "Obfuscated");
        this.add("screen.assortedhangeables.neon_sign.reset", "Reset");

        this.addManual();
    }

    /** This part's chapter of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.hanging", "On the Wall");

        this.add("manual.assorteddecor.chapter.hanging.wallpaper.title", "Wallpaper");
        this.add("manual.assorteddecor.chapter.hanging.wallpaper",
                "Wallpaper covers a wall without taking up the block. Once it is up, right click to cycle "
                        + "through the designs, and right click with a dye to color it." + BREAK
                        + "Wallpaper next to wallpaper tries to match its neighbours, so a whole wall lines up "
                        + "on its own.");

        this.add("manual.assorteddecor.chapter.hanging.frames.title", "Frames");
        this.add("manual.assorteddecor.chapter.hanging.frames",
                "Frames add depth where wallpaper adds pattern, in wood or iron, and right click to cycle through their "
                        + "patterns the same way." + BREAK
                        + "A frame is sturdier than wallpaper: take the block out from behind it and it stays "
                        + "where it is.");

        this.add("manual.assorteddecor.chapter.hanging.frame_info.title", "Dyeing Frames");
        this.add("manual.assorteddecor.chapter.hanging.frame_info",
                "Right click a hung frame with a dye and it takes that color, so a wall can be framed in one shade or in several.");

        this.add("manual.assorteddecor.chapter.hanging.calendar.title", "Calendar");
        this.add("manual.assorteddecor.chapter.hanging.calendar",
                "A calendar on the wall keeps count of the days you have been in the world.");

        this.add("manual.assorteddecor.chapter.hanging.clock.title", "Wall Clock");
        this.add("manual.assorteddecor.chapter.hanging.clock",
                "A wall clock shows the time of day.");

        this.add("manual.assorteddecor.chapter.hanging.neon_sign.title", "Neon Sign");
        this.add("manual.assorteddecor.chapter.hanging.neon_sign",
                "A neon sign works like an ordinary sign, except colored text glows and there is a proper "
                        + "editor for getting the layout right. Three backgrounds to choose from.");
    }
}
