package com.grim3212.assorted.decorations.common.helpers;

import com.grim3212.assorted.decorations.Family;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.items.DecorationsItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class DecorationsCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    public static void init() {
        // Three places in the tab, as it was when this was all one mod, with the lanterns and paths of other parts between them.
        SharedCreativeTabs.add(TAB, 700, () -> stacks(DecorationsBlocks.PLANTER_POT.get(), DecorationsItems.UNFIRED_PLANTER_POT.get()));
        SharedCreativeTabs.add(TAB, 800, () -> stacks(DecorationsItems.UNFIRED_CLAY_DECORATION.get(), DecorationsBlocks.CLAY_DECORATION.get(), DecorationsBlocks.BONE_DECORATION.get()));
        SharedCreativeTabs.add(TAB, 870, () -> stacks(DecorationsBlocks.FOUNTAIN.get()));
    }

    private static List<ItemStack> stacks(ItemLike... items) {
        return Arrays.stream(items).map(ItemStack::new).toList();
    }
}
