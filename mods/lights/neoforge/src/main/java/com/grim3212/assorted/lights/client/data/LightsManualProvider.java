package com.grim3212.assorted.lights.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lights.Constants;
import com.grim3212.assorted.lights.Family;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/** This part's chapter of the Assorted Decor manual section, which every part of the family shares. */
public class LightsManualProvider extends LibManualProvider {

    public LightsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        Block[] fluro = FluroBlock.FLURO_BY_DYE.values().stream().map(x -> x.get()).toArray(Block[]::new);
        Block[] illumination = {LightsBlocks.ILLUMINATION_TUBE.get(), LightsBlocks.ILLUMINATION_PLATE.get()};
        Block[] lanterns = {LightsBlocks.BONE_LANTERN.get(), LightsBlocks.IRON_LANTERN.get(), LightsBlocks.PAPER_LANTERN.get()};

        ChapterBuilder lights = this.chapter("lights", 4);
        lights.recipes("fluro", fluro).every(30).opens(fluro);
        lights.recipes("illumination", illumination).every(50).opens(illumination);
        lights.recipes("lanterns", lanterns).every(50).opens(lanterns);
    }
}
