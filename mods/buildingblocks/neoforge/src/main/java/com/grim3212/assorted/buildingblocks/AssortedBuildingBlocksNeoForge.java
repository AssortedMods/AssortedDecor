package com.grim3212.assorted.buildingblocks;

import com.grim3212.assorted.buildingblocks.client.data.BuildingBlocksLanguageProvider;
import com.grim3212.assorted.buildingblocks.client.data.BuildingBlocksManualProvider;
import com.grim3212.assorted.buildingblocks.client.data.BuildingBlocksBlockstateProvider;
import com.grim3212.assorted.buildingblocks.client.data.BuildingBlocksItemModelProvider;
import com.grim3212.assorted.buildingblocks.data.BuildingBlocksBlockLoot;
import com.grim3212.assorted.buildingblocks.data.BuildingBlocksBlockTagProvider;
import com.grim3212.assorted.buildingblocks.data.BuildingBlocksItemTagProvider;
import com.grim3212.assorted.buildingblocks.data.BuildingBlocksDataMapProvider;
import com.grim3212.assorted.buildingblocks.data.BuildingBlocksRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedBuildingBlocksNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedBuildingBlocksNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        BuildingBlocksCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new BuildingBlocksRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new BuildingBlocksDataMapProvider(packOutput, lookupProvider));
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new BuildingBlocksBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new BuildingBlocksItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BuildingBlocksBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /**
     * Client datagen: block states and models, item models and the lang file. The two model
     * providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new BuildingBlocksBlockstateProvider(packOutput));
        event.addProvider(new BuildingBlocksItemModelProvider(packOutput));
        event.addProvider(new BuildingBlocksLanguageProvider(packOutput));
        event.addProvider(new BuildingBlocksManualProvider(packOutput));
    }
}
