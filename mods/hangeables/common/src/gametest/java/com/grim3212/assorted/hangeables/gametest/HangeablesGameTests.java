package com.grim3212.assorted.hangeables.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Hangeables. The tests live in the {@code *Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedhangeables/test_instance/<name>.json}.
 */
public final class HangeablesGameTests {

    private HangeablesGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        EntityDecorationTests.register(out);
        BlockDecorationTests.register(out);
        FamilyTests.register(out);
    }
}
