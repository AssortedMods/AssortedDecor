package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerUnsided;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class AssortedDecorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DecorCommonMod.init();

        ItemStorage.SIDED.registerForBlockEntities((be, direction) ->
                {
                    if (be instanceof IInventoryBlockEntity inv)
                        return ((FabricPlatformInventoryStorageHandlerUnsided) inv.getStorageHandler()).getFabricInventory();
                    return null;
                },
                DecorBlockEntityTypes.CAGE.get()
        );

        registerCopperDisplayCaseOxidation();
    }

    /**
     * Tells Fabric how the copper display cases scrape back with an axe and wax with a honeycomb;
     * oxidising over time is {@code WeatheringDisplayCaseBlock}'s own random tick and does not read
     * this. NeoForge's half is the {@code neoforge:oxidizables} and {@code neoforge:waxables} data
     * maps in {@code DecorDataMapProvider}, so a change here needs the same change there.
     */
    private static void registerCopperDisplayCaseOxidation() {
        DecorBlocks.COPPER_DISPLAY_CASES.weathering().progressMapping((from, to) ->
                OxidizableBlocksRegistry.registerNextStage(from.get(), to.get()));
        DecorBlocks.COPPER_DISPLAY_CASES.zipUnwaxedWaxed((unwaxed, waxed) ->
                OxidizableBlocksRegistry.registerWaxable(unwaxed.get(), waxed.get()));
    }
}
