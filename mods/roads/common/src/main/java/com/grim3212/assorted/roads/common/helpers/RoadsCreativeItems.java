package com.grim3212.assorted.roads.common.helpers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.items.RoadsItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class RoadsCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getRoadwayItems() {
        CreativeTabItems items = new CreativeTabItems();

        RoadsBlocks.ROADWAY_COLORS.values().forEach(x -> items.add(x.get()));
        items.add(RoadsBlocks.ROADWAY.get());
        items.add(RoadsBlocks.ROADWAY_MANHOLE.get());
        items.add(RoadsBlocks.ROADWAY_LIGHT.get());
        items.add(RoadsItems.TARBALL.get());
        items.add(RoadsItems.ASPHALT.get());

        return items.getItems();
    }

    private static Supplier<List<ItemStack>> single(Supplier<? extends ItemLike> item) {
        return () -> {
            CreativeTabItems items = new CreativeTabItems();
            items.add(item.get());
            return items.getItems();
        };
    }

    public static void init() {
        // Where each of these sat in the tab when this was all one mod, among the decorations and then the roadways.
        SharedCreativeTabs.add(TAB, 880, single(RoadsBlocks.STONE_PATH));
        SharedCreativeTabs.add(TAB, 895, single(RoadsBlocks.SIDEWALK));
        SharedCreativeTabs.add(TAB, 1100, RoadsCreativeItems::getRoadwayItems);
    }
}
