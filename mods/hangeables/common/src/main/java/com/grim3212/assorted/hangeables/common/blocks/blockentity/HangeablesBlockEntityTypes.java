package com.grim3212.assorted.hangeables.common.blocks.blockentity;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.Family;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HangeablesBlockEntityTypes {
    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<NeonSignBlockEntity>> NEON_SIGN = BLOCK_ENTITIES.register("neon_sign", () -> Services.PLATFORM.createBlockEntityType(NeonSignBlockEntity::new, HangeablesBlocks.NEON_SIGN.get(), HangeablesBlocks.NEON_SIGN_WALL.get()));
    public static final IRegistryObject<BlockEntityType<CalendarBlockEntity>> CALENDAR = BLOCK_ENTITIES.register("calendar", () -> Services.PLATFORM.createBlockEntityType(CalendarBlockEntity::new, HangeablesBlocks.CALENDAR.get()));
    public static final IRegistryObject<BlockEntityType<WallClockBlockEntity>> WALL_CLOCK = BLOCK_ENTITIES.register("wall_clock", () -> Services.PLATFORM.createBlockEntityType(WallClockBlockEntity::new, HangeablesBlocks.WALL_CLOCK.get()));

    public static void init() {
    }
}
