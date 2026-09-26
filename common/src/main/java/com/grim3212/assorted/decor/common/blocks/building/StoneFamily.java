package com.grim3212.assorted.decor.common.blocks.building;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Everything cut from one kind of stone, vanilla's blocks where it already has them. The polished slab
 * is what two of stack into the carved block, or null where vanilla already uses that recipe.
 */
public record StoneFamily(String name, Supplier<Block> raw, Supplier<Block> polished, @Nullable Supplier<Block> polishedSlab,
                          WeatheredSet bricks, Supplier<Block> tiles, Supplier<Block> carved,
                          IRegistryObject<Block> fluted, IRegistryObject<Block> column) {
}
