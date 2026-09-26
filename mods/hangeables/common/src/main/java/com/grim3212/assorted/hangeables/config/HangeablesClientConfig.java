package com.grim3212.assorted.hangeables.config;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class HangeablesClientConfig {
    public final Supplier<Double> wallpaperWidth;

    public HangeablesClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        wallpaperWidth = builder.defineDouble("wallpaper.wallpaperWidth", 1.0D, 0.1D, 5.0D, "Set this to determine how much the wallpaper will stick off of the wall.");

        builder.setup();
    }
}
