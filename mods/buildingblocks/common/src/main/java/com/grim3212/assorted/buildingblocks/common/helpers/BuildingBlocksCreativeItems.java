package com.grim3212.assorted.buildingblocks.common.helpers;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.items.BuildingBlocksItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Decor tab, which every part asks for and the first to load registers. */
public class BuildingBlocksCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> decorativeStone() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(BuildingBlocksBlocks.DECORATIVE_STONE.get());
        return items.getItems();
    }

    private static List<ItemStack> chainLinkAndDoors() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(BuildingBlocksItems.CHAIN_LINK.get());
        items.add(BuildingBlocksBlocks.CHAIN_LINK_FENCE.get());
        items.add(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get());
        items.add(BuildingBlocksBlocks.QUARTZ_DOOR.get());
        items.add(BuildingBlocksBlocks.GLASS_DOOR.get());
        items.add(BuildingBlocksBlocks.STEEL_DOOR.get());
        return items.getItems();
    }

    private static List<ItemStack> sidings() {
        CreativeTabItems items = new CreativeTabItems();
        Arrays.stream(DyeColor.values()).forEach(x -> {
            items.add(ColorChangingBlock.getColorStack(new ItemStack(BuildingBlocksBlocks.SIDING_VERTICAL.get()), x));
            items.add(ColorChangingBlock.getColorStack(new ItemStack(BuildingBlocksBlocks.SIDING_HORIZONTAL.get()), x));
        });
        return items.getItems();
    }

    private static List<ItemStack> buildingBlocks() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(BuildingBlocksBlocks.LUMBER_MILL.get());
        BuildingBlocks.all().forEach(x -> items.add(x.get()));
        return items.getItems();
    }

    public static void init() {
        // Spread out among the other parts' items, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 890, BuildingBlocksCreativeItems::decorativeStone);
        SharedCreativeTabs.add(TAB, 1000, BuildingBlocksCreativeItems::chainLinkAndDoors);
        SharedCreativeTabs.add(TAB, 1250, BuildingBlocksCreativeItems::sidings);
        // Last, as there are hundreds of them and they would bury everything else.
        SharedCreativeTabs.add(TAB, 1300, BuildingBlocksCreativeItems::buildingBlocks);
    }
}
