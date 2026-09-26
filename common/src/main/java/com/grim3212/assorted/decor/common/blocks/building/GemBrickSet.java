package com.grim3212.assorted.decor.common.blocks.building;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.Block;

/** The three cuts the lapis and redstone blocks come in. */
public record GemBrickSet(IRegistryObject<Block> bricks, IRegistryObject<Block> tiles, IRegistryObject<Block> polished) {
}
