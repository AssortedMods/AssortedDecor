package com.grim3212.assorted.hangeables.common.helpers;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class HangeablesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    public static void init() {
        // Two places in the tab, as it was when this was all one mod: the sign after the lights, the rest after that.
        SharedCreativeTabs.add(TAB, 300, () -> stacks(HangeablesItems.NEON_SIGN.get()));
        SharedCreativeTabs.add(TAB, 400, () -> stacks(HangeablesBlocks.CALENDAR.get(), HangeablesBlocks.WALL_CLOCK.get(), HangeablesItems.WALLPAPER.get(), HangeablesItems.WOOD_FRAME.get(), HangeablesItems.IRON_FRAME.get()));
    }

    private static List<ItemStack> stacks(ItemLike... items) {
        return Arrays.stream(items).map(ItemStack::new).toList();
    }
}
