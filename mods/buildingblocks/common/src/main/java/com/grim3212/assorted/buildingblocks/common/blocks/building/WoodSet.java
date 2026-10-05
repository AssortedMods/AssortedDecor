package com.grim3212.assorted.buildingblocks.common.blocks.building;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** The parquet, framed planks, panel and beam made from one kind of planks. Nether woods do not burn. */
public record WoodSet(String name, Supplier<Block> planks, Supplier<Block> slab, boolean flammable,
                      IRegistryObject<Block> parquet, IRegistryObject<Block> framed,
                      IRegistryObject<Block> panel, IRegistryObject<Block> beam) {
}
