package com.grim3212.assorted.gates.client.data;

import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;

/**
 * This part's chapter of the Assorted Decor section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every block and item has to open a page, or the provider refuses to generate.
 */
public class GatesManualProvider extends LibManualProvider {

    public GatesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder chapter = this.chapter("gates", 8);
        chapter.recipes("castle_gate", GatesItems.GATE_GRATING.get(), GatesBlocks.CASTLE_GATE.get(), GatesItems.GATE_TRUMPET.get()).every(60)
                .opens(GatesBlocks.CASTLE_GATE.get(), GatesItems.GATE_GRATING.get(), GatesItems.GATE_TRUMPET.get());
        chapter.recipes("garage_door", GatesItems.GARAGE_PANEL.get(), GatesBlocks.GARAGE_DOOR.get(), GatesItems.GARAGE_REMOTE.get()).every(60)
                .opens(GatesBlocks.GARAGE_DOOR.get(), GatesItems.GARAGE_PANEL.get(), GatesItems.GARAGE_REMOTE.get());
    }
}
