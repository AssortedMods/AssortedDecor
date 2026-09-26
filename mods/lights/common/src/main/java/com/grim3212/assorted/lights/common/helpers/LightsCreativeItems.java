package com.grim3212.assorted.lights.common.helpers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lights.Family;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class LightsCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getFluroItems() {
        CreativeTabItems items = new CreativeTabItems();

        FluroBlock.FLURO_BY_DYE.values().forEach(x -> items.add(x.get()));
        items.add(LightsBlocks.ILLUMINATION_TUBE.get());
        items.add(LightsBlocks.ILLUMINATION_PLATE.get());

        return items.getItems();
    }

    private static List<ItemStack> getLanternItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(LightsBlocks.PAPER_LANTERN.get());
        items.add(LightsBlocks.BONE_LANTERN.get());
        items.add(LightsBlocks.IRON_LANTERN.get());

        return items.getItems();
    }

    public static void init() {
        // The two places these sat in the tab when this was all one mod: after the colorizers, and among the decorations.
        SharedCreativeTabs.add(TAB, 200, LightsCreativeItems::getFluroItems);
        SharedCreativeTabs.add(TAB, 850, LightsCreativeItems::getLanternItems);
    }
}
