package com.grim3212.assorted.decor.common.blocks.building;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** A pattern with its mossy and cracked forms, as vanilla's stone bricks come; any of the three may be vanilla's own. */
public record WeatheredSet(Supplier<Block> plain, Supplier<Block> mossy, Supplier<Block> cracked) {
}
