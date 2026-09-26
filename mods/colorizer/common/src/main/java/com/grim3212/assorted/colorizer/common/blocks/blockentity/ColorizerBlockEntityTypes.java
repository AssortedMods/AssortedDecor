package com.grim3212.assorted.colorizer.common.blocks.blockentity;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.Family;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ColorizerBlockEntityTypes {
    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<ColorizerBlockEntity>> COLORIZER = BLOCK_ENTITIES.register("colorizer", () -> Services.PLATFORM.createBlockEntityType(ColorizerBlockEntity::new, ColorizerBlocks.colorizerBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new)));

    public static void init() {
    }
}
