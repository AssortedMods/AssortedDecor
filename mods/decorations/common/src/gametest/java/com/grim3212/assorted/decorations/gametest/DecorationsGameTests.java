package com.grim3212.assorted.decorations.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Decorations. The tests live in the {@code *Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assorteddecorations/test_instance/<name>.json}.
 */
public final class DecorationsGameTests {

    private DecorationsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        BlockDecorationTests.register(out);
        FamilyTests.register(out);
    }
}
