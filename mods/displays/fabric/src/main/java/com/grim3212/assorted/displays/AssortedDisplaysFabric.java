package com.grim3212.assorted.displays;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.blocks.blockentity.DisplaysBlockEntityTypes;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerUnsided;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class AssortedDisplaysFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DisplaysCommonMod.init();

        ItemStorage.SIDED.registerForBlockEntities((be, direction) ->
                {
                    if (be instanceof IInventoryBlockEntity inv)
                        return ((FabricPlatformInventoryStorageHandlerUnsided) inv.getStorageHandler()).getFabricInventory();
                    return null;
                },
                DisplaysBlockEntityTypes.CAGE.get()
        );

        registerCopperDisplayCaseOxidation();
    }

    /**
     * Tells Fabric how the copper display cases scrape back with an axe and wax with a honeycomb;
     * oxidising over time is {@code WeatheringDisplayCaseBlock}'s own random tick and does not read
     * this. NeoForge's half is the {@code neoforge:oxidizables} and {@code neoforge:waxables} data
     * maps in {@code DisplaysDataMapProvider}, so a change here needs the same change there.
     */
    private static void registerCopperDisplayCaseOxidation() {
        DisplaysBlocks.COPPER_DISPLAY_CASES.weathering().progressMapping((from, to) ->
                OxidizableBlocksRegistry.registerNextStage(from.get(), to.get()));
        DisplaysBlocks.COPPER_DISPLAY_CASES.zipUnwaxedWaxed((unwaxed, waxed) ->
                OxidizableBlocksRegistry.registerWaxable(unwaxed.get(), waxed.get()));
    }
}
