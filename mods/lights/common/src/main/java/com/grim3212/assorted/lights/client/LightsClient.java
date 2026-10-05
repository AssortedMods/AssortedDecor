package com.grim3212.assorted.lights.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lights.client.color.BlockMapColorItemTintSource;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import net.minecraft.util.ARGB;

import java.util.stream.Collectors;

public class LightsClient {

    public static void init() {
        // The sixteen fluro blocks share one texture, tinted by their own map colour.
        ClientServices.CLIENT.registerBlockColor(state -> ARGB.opaque(state.getBlock().defaultMapColor().col), () -> FluroBlock.FLURO_BY_DYE.values().stream().map(x -> x.get()).collect(Collectors.toList()));

        // The generated fluro item models name this in their "tints" lists.
        ClientServices.CLIENT.registerItemTintSource(BlockMapColorItemTintSource.ID, BlockMapColorItemTintSource.MAP_CODEC);
    }
}
