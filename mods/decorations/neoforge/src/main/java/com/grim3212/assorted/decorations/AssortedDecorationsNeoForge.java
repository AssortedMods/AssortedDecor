package com.grim3212.assorted.decorations;

import com.grim3212.assorted.decorations.client.data.DecorationsLanguageProvider;
import com.grim3212.assorted.decorations.client.data.DecorationsManualProvider;
import com.grim3212.assorted.decorations.client.data.DecorationsBlockstateProvider;
import com.grim3212.assorted.decorations.client.data.DecorationsItemModelProvider;
import com.grim3212.assorted.decorations.data.DecorationsBlockLoot;
import com.grim3212.assorted.decorations.data.DecorationsBlockTagProvider;
import com.grim3212.assorted.decorations.data.DecorationsRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
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
public class AssortedDecorationsNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedDecorationsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        DecorationsCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new DecorationsRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new DecorationsBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(DecorationsBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /**
     * Client datagen: block states and models, item models and the lang file. The two model
     * providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new DecorationsBlockstateProvider(packOutput));
        event.addProvider(new DecorationsItemModelProvider(packOutput));
        event.addProvider(new DecorationsLanguageProvider(packOutput));
        event.addProvider(new DecorationsManualProvider(packOutput));
    }
}
