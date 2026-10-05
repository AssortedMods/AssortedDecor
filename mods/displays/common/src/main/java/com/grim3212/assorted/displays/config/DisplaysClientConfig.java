package com.grim3212.assorted.displays.config;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class DisplaysClientConfig {
    public final Supplier<Double> cageSpinMod;

    public DisplaysClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        cageSpinMod = builder.defineDouble("cage.cageSpinMod", 3.0D, 1.0D, 20.0D, "This is the modifier for how fast entities spin when displayed inside the Cage.");

        builder.setup();
    }
}
