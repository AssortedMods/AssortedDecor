package com.grim3212.assorted.gates;

import com.grim3212.assorted.gates.client.data.GatesLanguageProvider;
import com.grim3212.assorted.gates.client.data.GatesManualProvider;
import com.grim3212.assorted.gates.client.data.GatesBlockstateProvider;
import com.grim3212.assorted.gates.client.data.GatesItemModelProvider;
import com.grim3212.assorted.gates.data.GatesBlockLoot;
import com.grim3212.assorted.gates.data.GatesBlockTagProvider;
import com.grim3212.assorted.gates.data.GatesRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedGatesNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedGatesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        GatesCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(GatesRecipes::new).blockLoot(GatesBlockLoot::new).addTo(event);
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new GatesBlockTagProvider(packOutput, lookupProvider)));
    }

    /**
     * Client datagen: block states and models, item models and the lang file. The two model
     * providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new GatesBlockstateProvider(packOutput));
        event.addProvider(new GatesItemModelProvider(packOutput));
        event.addProvider(new GatesLanguageProvider(packOutput));
        event.addProvider(new GatesManualProvider(packOutput));
    }
}
