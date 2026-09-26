package com.grim3212.assorted.gates.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.gates.Family;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.sounds.GatesSounds;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Decor, still has these gates and their items in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assorteddecor_ids_still_load", AliasTests::assorteddecorIdsStillLoad);
    }

    private static void assorteddecorIdsStillLoad(GameTestHelper helper) {
        // Every item, the trumpet and remote included, registers through GatesBlocks.ITEMS.
        for (IRegistryObject<Item> item : GatesBlocks.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<Block> block : GatesBlocks.BLOCKS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(old(block.getId())), block.get(), "the block saved as " + old(block.getId()));
        }

        for (IRegistryObject<SoundEvent> sound : GatesSounds.SOUNDS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.SOUND_EVENT.getValue(old(sound.getId())), sound.get(), "the sound saved as " + old(sound.getId()));
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Family.ID, id.getPath());
    }
}
