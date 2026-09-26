package com.grim3212.assorted.colorizer.common.helpers;

import com.grim3212.assorted.colorizer.Family;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class ColorizerCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(ColorizerItems.COLORIZER_BRUSH.get());
        ColorizerBlocks.colorizerBlocks().forEach(x -> items.add(x.get()));
        return items.getItems();
    }

    public static void init() {
        // First in the tab, as it was when this was all one mod.
        SharedCreativeTabs.add(TAB, 100, ColorizerCreativeItems::getCreativeItems);
    }
}
