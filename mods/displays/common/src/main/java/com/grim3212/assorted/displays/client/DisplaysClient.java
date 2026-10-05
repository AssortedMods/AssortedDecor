package com.grim3212.assorted.displays.client;

import com.grim3212.assorted.displays.client.blockentity.CageBlockEntityRenderer;
import com.grim3212.assorted.displays.client.blockentity.DisplayCaseBlockEntityRenderer;
import com.grim3212.assorted.displays.client.screen.CageScreen;
import com.grim3212.assorted.displays.common.blocks.blockentity.DisplaysBlockEntityTypes;
import com.grim3212.assorted.displays.common.inventory.DisplaysContainerTypes;
import com.grim3212.assorted.displays.config.DisplaysClientConfig;
import com.grim3212.assorted.lib.platform.ClientServices;

public class DisplaysClient {

    public static final DisplaysClientConfig CLIENT_CONFIG = new DisplaysClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerScreen(DisplaysContainerTypes.CAGE::get, CageScreen::new);

        ClientServices.CLIENT.registerBlockEntityRenderer(DisplaysBlockEntityTypes.CAGE::get, CageBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(DisplaysBlockEntityTypes.DISPLAY_CASE::get, DisplayCaseBlockEntityRenderer::new);
    }
}
