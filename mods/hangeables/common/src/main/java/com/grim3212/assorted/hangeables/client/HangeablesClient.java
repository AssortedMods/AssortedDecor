package com.grim3212.assorted.hangeables.client;

import com.grim3212.assorted.hangeables.client.blockentity.CalendarBlockEntityRenderer;
import com.grim3212.assorted.hangeables.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.hangeables.client.render.entity.FrameRenderer;
import com.grim3212.assorted.hangeables.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.HangeablesBlockEntityTypes;
import com.grim3212.assorted.hangeables.common.entity.HangeablesEntityTypes;
import com.grim3212.assorted.hangeables.config.HangeablesClientConfig;
import com.grim3212.assorted.lib.platform.ClientServices;

public class HangeablesClient {

    public static final HangeablesClientConfig CLIENT_CONFIG = new HangeablesClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerEntityRenderer(HangeablesEntityTypes.WALLPAPER::get, WallpaperRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(HangeablesEntityTypes.WOOD_FRAME::get, FrameRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(HangeablesEntityTypes.IRON_FRAME::get, FrameRenderer::new);

        ClientServices.CLIENT.registerBlockEntityRenderer(HangeablesBlockEntityTypes.NEON_SIGN::get, NeonSignBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(HangeablesBlockEntityTypes.CALENDAR::get, CalendarBlockEntityRenderer::new);
    }
}
