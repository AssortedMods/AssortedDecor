package com.grim3212.assorted.displays.common.blocks.blockentity;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DisplaysBlockEntityTypes {
    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<BlockEntityType<CageBlockEntity>> CAGE = BLOCK_ENTITIES.register("cage", () -> Services.PLATFORM.createBlockEntityType(CageBlockEntity::new, DisplaysBlocks.CAGE.get()));
    public static final IRegistryObject<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE = BLOCK_ENTITIES.register("display_case", () -> Services.PLATFORM.createBlockEntityType(DisplayCaseBlockEntity::new, DisplaysBlocks.displayCaseBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new)));

    public static void init() {
    }
}
