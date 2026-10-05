package com.grim3212.assorted.gates.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.gates.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line here
 * (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class GatesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public GatesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("assortedgates.subtitle.gate_trumpet", "Gate trumpet blows");
        this.add("assortedgates.subtitle.garage_remote", "Garage remote clicks");

        this.addManual();
    }

    /** This part's chapter of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.add("manual.assorteddecor.chapter.gates", "Gates");

        this.add("manual.assorteddecor.chapter.gates.castle_gate.title", "Castle Gate");
        this.add("manual.assorteddecor.chapter.gates.castle_gate",
                "Place a castle gate against the underside of a solid block and it drops all the way "
                        + "down to the ground. Gates side by side and facing the same way will open and close as one." + BREAK
                        + "Blow the gate trumpet to open it up from afar." + BREAK
                        + "Redstone at the top block holds it open while it is powered.");

        this.add("manual.assorteddecor.chapter.gates.garage_door.title", "Garage Door");
        this.add("manual.assorteddecor.chapter.gates.garage_door",
                "A garage door hangs and fills down the same way a castle gate does, as a solid panel rather than "
                        + "bars. The garage remote opens and closes it, and redstone works on it too.");
    }
}
