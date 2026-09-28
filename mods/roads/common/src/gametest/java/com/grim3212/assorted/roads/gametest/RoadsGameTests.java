package com.grim3212.assorted.roads.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Roads. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedroads/test_instance/<name>.json}.
 */
public final class RoadsGameTests {

    private RoadsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        RoadTests.register(out);
        CrossLoaderDataTests.register(out);
        AssetTests.register(out);
        AliasTests.register(out);
        FamilyTests.register(out);
    }
}
