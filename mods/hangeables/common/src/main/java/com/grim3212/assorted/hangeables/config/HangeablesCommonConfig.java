package com.grim3212.assorted.hangeables.config;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class HangeablesCommonConfig {

    public final Supplier<Boolean> framesBurn;
    public final Supplier<Boolean> dyeFrames;
    public final Supplier<Boolean> wallpapersBurn;
    public final Supplier<Boolean> dyeWallpapers;
    public final Supplier<Boolean> wallpapersCopyDye;
    public final Supplier<Integer> numWallpaperOptions;

    public HangeablesCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        framesBurn = builder.defineBoolean("frames.framesBurn", true, "Set this to true if you want frames to be able to get burnt");
        dyeFrames = builder.defineBoolean("frames.dyeFrames", true, "Set this to true if you want to be able to dye Frames");

        wallpapersBurn = builder.defineBoolean("wallpaper.wallpapersBurn", true, "Set this to true if you want wallpaper to be able to get burnt");
        dyeWallpapers = builder.defineBoolean("wallpaper.dyeWallpapers", true, "Set this to true if you want to be able to dye wallpaper");
        wallpapersCopyDye = builder.defineBoolean("wallpaper.wallpapersCopyDye", true, "Set this to true if you want wallpaper to be able to copy dye colors from adjacent wallpaper");
        numWallpaperOptions = builder.defineInteger("wallpaper.numWallpaperOptions", 24, 1, 256, "Set this to the amount of wallpapers currently defined on the texture");

        builder.setup();
    }
}
