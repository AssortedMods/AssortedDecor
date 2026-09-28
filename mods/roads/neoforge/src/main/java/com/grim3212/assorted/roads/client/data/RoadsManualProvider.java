package com.grim3212.assorted.roads.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.items.RoadsItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/** This part's chapter of the Assorted Decor manual section, which every part of the family shares. */
public class RoadsManualProvider extends LibManualProvider {

    public RoadsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        List<Block> roadway = new ArrayList<>(List.of(RoadsBlocks.ROADWAY.get(), RoadsBlocks.ROADWAY_LIGHT.get(),
                RoadsBlocks.ROADWAY_MANHOLE.get()));
        RoadsBlocks.ROADWAY_COLORS.values().stream().map(IRegistryObject::get).forEach(roadway::add);

        ChapterBuilder roads = this.chapter("roads", 6);
        roads.recipes("roadway", RoadsBlocks.ROADWAY.get(), RoadsBlocks.ROADWAY_MANHOLE.get()).every(50)
                .opens(roadway.toArray(Block[]::new));
        // Its plate comes from Assorted Lights, so without it there is no recipe to draw.
        roads.recipes("roadway_light", RoadsBlocks.ROADWAY_LIGHT.get())
                .when(modLoaded("assortedlights"));
        roads.recipes("asphalt", RoadsItems.ASPHALT.get()).opens(RoadsItems.ASPHALT.get(), RoadsItems.TARBALL.get());
        roads.recipes("sidewalk", RoadsBlocks.SIDEWALK.get()).opens(RoadsBlocks.SIDEWALK.get());
        // The stone path comes off the stonecutter rather than a bench.
        roads.recipesById("stone_path", recipeId("stone_path_stonecutting"))
                .opens(RoadsBlocks.STONE_PATH.get());
    }
}
