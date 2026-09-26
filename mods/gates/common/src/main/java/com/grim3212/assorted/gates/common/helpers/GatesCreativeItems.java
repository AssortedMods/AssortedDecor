package com.grim3212.assorted.gates.common.helpers;

import com.grim3212.assorted.gates.Family;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class GatesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(GatesItems.GATE_GRATING.get());
        items.add(GatesBlocks.CASTLE_GATE.get());
        items.add(GatesItems.GATE_TRUMPET.get());
        items.add(GatesItems.GARAGE_PANEL.get());
        items.add(GatesBlocks.GARAGE_DOOR.get());
        items.add(GatesItems.GARAGE_REMOTE.get());

        return items.getItems();
    }

    public static void init() {
        // After the sidewalk and before the chain link and doors, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 900, GatesCreativeItems::getCreativeItems);
    }
}
