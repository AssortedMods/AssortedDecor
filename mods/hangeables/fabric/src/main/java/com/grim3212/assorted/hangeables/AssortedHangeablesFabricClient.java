package com.grim3212.assorted.hangeables;

import com.grim3212.assorted.hangeables.client.HangeablesClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedHangeablesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HangeablesClient.init();
    }
}
