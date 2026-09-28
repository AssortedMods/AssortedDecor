package com.grim3212.assorted.colorizer.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Colorizer. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedcolorizer/test_instance/<name>.json}.
 */
public final class ColorizerGameTests {

    private ColorizerGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        ColorizerTests.register(out);
        LightTests.register(out);
        LightBenchmark.register(out);
        TooltipTests.register(out);
        FamilyTests.register(out);
    }
}
