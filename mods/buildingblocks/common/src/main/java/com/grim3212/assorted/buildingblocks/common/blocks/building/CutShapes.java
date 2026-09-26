package com.grim3212.assorted.buildingblocks.common.blocks.building;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import org.jetbrains.annotations.Nullable;

/** The slab, stairs and, for masonry, wall cut from one building block. */
public record CutShapes(IRegistryObject<SlabBlock> slab, IRegistryObject<StairBlock> stairs, @Nullable IRegistryObject<WallBlock> wall) {
}
