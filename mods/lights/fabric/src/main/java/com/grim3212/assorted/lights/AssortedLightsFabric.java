package com.grim3212.assorted.lights;

import net.fabricmc.api.ModInitializer;

public class AssortedLightsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LightsCommonMod.init();
    }
}
