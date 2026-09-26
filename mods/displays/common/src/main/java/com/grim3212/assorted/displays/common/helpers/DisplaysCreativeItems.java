package com.grim3212.assorted.displays.common.helpers;

import com.grim3212.assorted.displays.Family;
import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.items.DisplaysItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class DisplaysCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCage() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(DisplaysBlocks.CAGE.get());
        return items.getItems();
    }

    private static List<ItemStack> getDisplayCases() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(DisplaysItems.RESIZING_TOOL.get());
        DisplaysBlocks.displayCaseBlocks().forEach(x -> items.add(x.get()));
        return items.getItems();
    }

    public static void init() {
        // After the wall hangings and before the planter pots, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 500, DisplaysCreativeItems::getCage);
        SharedCreativeTabs.add(TAB, 600, DisplaysCreativeItems::getDisplayCases);
    }
}
