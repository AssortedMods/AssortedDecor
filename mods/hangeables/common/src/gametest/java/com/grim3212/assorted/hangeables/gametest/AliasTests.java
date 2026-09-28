package com.grim3212.assorted.hangeables.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.HangeablesBlockEntityTypes;
import com.grim3212.assorted.hangeables.common.entity.HangeablesEntityTypes;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Decor, still has these signs, clocks, frames and wallpaper in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assorteddecor_ids_still_load", AliasTests::assorteddecorIdsStillLoad);
    }

    private static void assorteddecorIdsStillLoad(GameTestHelper helper) {
        // Every item registers through HangeablesBlocks.ITEMS, the block items and the plain ones alike.
        for (IRegistryObject<Item> item : HangeablesBlocks.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<Block> block : HangeablesBlocks.BLOCKS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(old(block.getId())), block.get(), "the block saved as " + old(block.getId()));
        }

        for (IRegistryObject<BlockEntityType<?>> type : HangeablesBlockEntityTypes.BLOCK_ENTITIES.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(old(type.getId())), type.get(), "the block entity saved as " + old(type.getId()));
        }

        for (IRegistryObject<EntityType<?>> type : HangeablesEntityTypes.ENTITIES.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.ENTITY_TYPE.getValue(old(type.getId())), type.get(), "the entity saved as " + old(type.getId()));
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, id.getPath());
    }
}
