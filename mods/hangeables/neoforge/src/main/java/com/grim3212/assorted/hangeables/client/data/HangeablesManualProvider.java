package com.grim3212.assorted.hangeables.client.data;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.Family;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Decor section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class HangeablesManualProvider extends LibManualProvider {

    public HangeablesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder hanging = this.chapter("hanging", 5);
        hanging.recipes("wallpaper", HangeablesItems.WALLPAPER.get()).opens(HangeablesItems.WALLPAPER.get());
        hanging.recipes("frames", HangeablesItems.WOOD_FRAME.get(), HangeablesItems.IRON_FRAME.get()).every(50)
                .opens(HangeablesItems.WOOD_FRAME.get(), HangeablesItems.IRON_FRAME.get());
        // New page rather than replacing the recipe: the shot shows what dyeing them looks like.
        hanging.image("frame_info", picture("frames"), 108, 104);
        hanging.recipes("calendar", HangeablesBlocks.CALENDAR.get()).opens(HangeablesBlocks.CALENDAR.get());
        hanging.recipes("clock", HangeablesBlocks.WALL_CLOCK.get()).opens(HangeablesBlocks.WALL_CLOCK.get());
        // Both sign blocks share one item, whose id is the standing block's, so listing the blocks
        // covers it.
        hanging.recipes("neon_sign", HangeablesBlocks.NEON_SIGN.get())
                .opens(HangeablesBlocks.NEON_SIGN.get(), HangeablesBlocks.NEON_SIGN_WALL.get());
    }

    /** The screenshots under {@code textures/gui/manual}, sized to leave room for the text below. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
