package com.grim3212.assorted.colorizer.config;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class ColorizerCommonConfig {

    public final Supplier<Boolean> colorizerConsumeBlock;
    public final Supplier<Integer> colorizerBrushCount;
    public final Supplier<Integer> shapeSmoothness;

    public ColorizerCommonConfig() {
        // NEEDED_AT_REGISTRATION: shapeSmoothness is read as blocks bake their shapes, before a NOT_SYNCED config loads.
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NEEDED_AT_REGISTRATION, Constants.MOD_ID + "-common");

        colorizerConsumeBlock = builder.defineBoolean("colorizer.colorizerConsumeBlock", true, "Set this to true if the colorizer brush should consume a block when using them");
        colorizerBrushCount = builder.defineInteger("colorizer.colorizerBrushCount", 16, 1, 400, "Set this to the amount of blocks that a brush will be able to colorize after grabbing a block");
        shapeSmoothness = builder.defineInteger("colorizer.shapeSmoothness", 2, 1, 20, "Set this to determine how smooth all of the different slopes collision boxes should be");

        builder.setup();
    }
}
