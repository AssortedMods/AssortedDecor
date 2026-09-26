package com.grim3212.assorted.displays.data;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.concurrent.CompletableFuture;

/**
 * Axe scraping and honeycomb waxing for the copper cases. Mirror of the {@code OxidizableBlocksRegistry}
 * calls in {@code AssortedDisplaysFabric}.
 */
public class DisplaysDataMapProvider extends DataMapProvider {

    public DisplaysDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        Builder<Oxidizable, Block> oxidizables = builder(NeoForgeDataMaps.OXIDIZABLES);
        DisplaysBlocks.COPPER_DISPLAY_CASES.weathering().progressMapping((from, to) ->
                oxidizables.add(key(from), new Oxidizable(to.get()), false));

        Builder<Waxable, Block> waxables = builder(NeoForgeDataMaps.WAXABLES);
        DisplaysBlocks.COPPER_DISPLAY_CASES.zipUnwaxedWaxed((unwaxed, waxed) ->
                waxables.add(key(unwaxed), new Waxable(waxed.get()), false));
    }

    private static ResourceKey<Block> key(IRegistryObject<? extends Block> block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block.get()));
    }
}
