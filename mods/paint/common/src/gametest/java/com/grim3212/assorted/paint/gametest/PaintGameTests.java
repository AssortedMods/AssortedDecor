package com.grim3212.assorted.paint.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Paint. The tests live in the {@code *Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedpaint/test_instance/<name>.json}.
 */
public final class PaintGameTests {

    private PaintGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        PaintTests.register(out);
        FamilyTests.register(out);
    }
}
