package com.grim3212.assorted.displays;

import com.grim3212.assorted.displays.client.DisplaysClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedDisplaysFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplaysClient.init();
    }
}
