package com.grim3212.assorted.decorations.client.data;

import com.grim3212.assorted.decorations.Constants;
import com.grim3212.assorted.decorations.Family;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.items.DecorationsItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Decor section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class DecorationsManualProvider extends LibManualProvider {

    public DecorationsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder decorations = this.chapter("decorations", 7);
        decorations.recipes("clay", DecorationsItems.UNFIRED_CLAY_DECORATION.get(), DecorationsBlocks.CLAY_DECORATION.get()).every(50)
                .opens(DecorationsBlocks.CLAY_DECORATION.get())
                .opens(DecorationsItems.UNFIRED_CLAY_DECORATION.get());
        decorations.recipes("planter_pot", DecorationsItems.UNFIRED_PLANTER_POT.get(), DecorationsBlocks.PLANTER_POT.get()).every(50)
                .opens(DecorationsBlocks.PLANTER_POT.get())
                .opens(DecorationsItems.UNFIRED_PLANTER_POT.get());
        decorations.recipes("bone", DecorationsBlocks.BONE_DECORATION.get()).opens(DecorationsBlocks.BONE_DECORATION.get());
        decorations.recipes("fountain", DecorationsBlocks.FOUNTAIN.get()).opens(DecorationsBlocks.FOUNTAIN.get());
    }
}
