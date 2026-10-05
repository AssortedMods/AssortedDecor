package com.grim3212.assorted.displays;

import com.grim3212.assorted.displays.client.data.DisplaysLanguageProvider;
import com.grim3212.assorted.displays.client.data.DisplaysManualProvider;
import com.grim3212.assorted.displays.client.data.DisplaysBlockstateProvider;
import com.grim3212.assorted.displays.client.data.DisplaysItemModelProvider;
import com.grim3212.assorted.displays.common.blocks.blockentity.DisplaysBlockEntityTypes;
import com.grim3212.assorted.displays.data.DisplaysBlockLoot;
import com.grim3212.assorted.displays.data.DisplaysBlockTagProvider;
import com.grim3212.assorted.displays.data.DisplaysDataMapProvider;
import com.grim3212.assorted.displays.data.DisplaysRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandlerUnsided;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedDisplaysNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedDisplaysNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);
        modBus.addListener(this::registerCapabilities);

        DisplaysCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new DisplaysRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new DisplaysDataMapProvider(packOutput, lookupProvider));
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new DisplaysBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(DisplaysBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /**
     * Client datagen: block states and models, item models and the lang file. The two model
     * providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new DisplaysBlockstateProvider(packOutput));
        event.addProvider(new DisplaysItemModelProvider(packOutput));
        event.addProvider(new DisplaysLanguageProvider(packOutput));
        event.addProvider(new DisplaysManualProvider(packOutput));
    }

    /**
     * Exposes the cage's inventory as {@code Capabilities.Item.BLOCK} through the library's unsided
     * handler, the NeoForge side of Fabric's {@code ItemStorage.SIDED}.
     */
    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, DisplaysBlockEntityTypes.CAGE.get(), (blockEntity, side) -> {
            if (blockEntity.isRemoved()) {
                return null;
            }
            return ((ForgePlatformInventoryStorageHandlerUnsided) blockEntity.getStorageHandler()).getCapability();
        });
    }
}
