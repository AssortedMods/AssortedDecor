package com.grim3212.assorted.paint.common.helpers;

import com.grim3212.assorted.paint.Family;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class PaintCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    public static void init() {
        SharedCreativeTabs.add(TAB, 1200, PaintCreativeItems::rollers);
    }

    private static List<ItemStack> rollers() {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(PaintItems.PAINT_ROLLER.get()));
        PaintItems.PAINT_ROLLER_COLORS.values().forEach(roller -> items.add(new ItemStack(roller.get())));
        return items;
    }
}
