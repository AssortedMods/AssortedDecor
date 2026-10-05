package com.grim3212.assorted.colorizer.gametest;

import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * How a colorizer is drawn and lit on the client, and the brush's tooltip as Fabric builds it: what a
 * headless server cannot see. Each drawing check compares the particle the item and block paths share.
 */
public class ColorizerClientGameTests implements FabricClientGameTest {

    private static final Identifier GOLD = Identifier.withDefaultNamespace("block/gold_block");
    private static final Identifier MISSING = Identifier.withDefaultNamespace("missingno");

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            context.runOnClient(client -> {
                // A colorizer item holding gold throws gold particles when it is dropped, broken or
                // eaten by lava - the particle comes from what the item draws, not the empty colorizer.
                ItemStack filled = new ItemStack(ColorizerBlocks.COLORIZER.get());
                NBTHelper.putTag(filled, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
                Identifier filledParticle = itemParticle(client, filled);
                if (!GOLD.equals(filledParticle)) {
                    throw new AssertionError("a colorizer item holding gold throws " + filledParticle + " particles, not " + GOLD);
                }

                // So the check above can only pass by reading the stack.
                Identifier emptyParticle = itemParticle(client, new ItemStack(ColorizerBlocks.COLORIZER.get()));
                if (GOLD.equals(emptyParticle)) {
                    throw new AssertionError("an empty colorizer item throws gold particles");
                }

                // Fabric only adds component tooltip lines on the client.
                ItemStack brush = new ItemStack(ColorizerItems.COLORIZER_BRUSH.get());
                NBTHelper.putTag(brush, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
                List<String> brushTooltip = brush.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL).stream()
                        .map(line -> line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString())
                        .toList();
                if (!brushTooltip.contains("tooltip.colorizer_brush.stored")) {
                    throw new AssertionError("a brush holding gold has the tooltip " + brushTooltip);
                }
            });

            // A placed colorizer, filled on the server and synced to the client, has to break into
            // gold as well: this goes through the blockstate's assortedlib:specification type and the
            // model json's loader, both of which Fabric only reads from "fabric:type".
            BlockPos pos = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(2);
                level.setBlockAndUpdate(at, ColorizerBlocks.COLORIZER.get().defaultBlockState());
                ((ColorizerBlockEntity) level.getBlockEntity(at)).setStoredBlockState(Blocks.GOLD_BLOCK.defaultBlockState());
                return at;
            });
            context.waitFor(client -> client.level != null
                    && client.level.getBlockEntity(pos) instanceof ColorizerBlockEntity colorizer
                    && colorizer.getStoredBlockState().is(Blocks.GOLD_BLOCK));

            Identifier placedParticle = context.computeOnClient(client -> {
                BlockState state = client.level.getBlockState(pos);
                return client.getModelManager().getBlockStateModelSet().getParticleMaterial(state, client.level, pos).sprite().contents().name();
            });
            if (!GOLD.equals(placedParticle)) {
                throw new AssertionError("a placed colorizer holding gold breaks into " + placedParticle + " particles, not " + GOLD);
            }

            // A colorizer somebody else filled. The server changes only the block entity - a full
            // cube dampens light like stone whether it is empty or holds glowstone, so its block
            // state never changes - and whole-chunk light goes only to players a chunk is on the
            // tracked border for, which this one is not. The client therefore has to relight from
            // the block entity it was sent, or the glowstone inside stays dark for every player but
            // the one who put it there.
            BlockPos litPos = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(6);
                level.setBlockAndUpdate(at, ColorizerBlocks.COLORIZER.get().defaultBlockState());
                return at;
            });
            // The client has to see the colorizer placed and empty first. Filling one that arrives
            // in the same breath as the block proves nothing: the block state change queues a light
            // check of its own, and the stored block is there by the time that check runs.
            context.waitFor(client -> client.level != null
                    && client.level.getBlockEntity(litPos) instanceof ColorizerBlockEntity colorizer
                    && colorizer.getStoredBlockState().isAir());

            world.getServer().runOnServer(server -> ((ColorizerBlockEntity) server.overworld().getBlockEntity(litPos))
                    .setStoredBlockState(Blocks.GLOWSTONE.defaultBlockState()));
            context.waitFor(client -> client.level.getBlockEntity(litPos) instanceof ColorizerBlockEntity colorizer
                    && colorizer.getStoredBlockState().is(Blocks.GLOWSTONE));
            // The relight is queued as the block entity arrives and runs with the client's own light
            // updates on a later tick.
            context.waitTicks(5);

            int clientLight = context.computeOnClient(client -> client.level.getBrightness(LightLayer.BLOCK, litPos));
            if (clientLight != 15) {
                throw new AssertionError("a colorizer holding glowstone lights this client at " + clientLight + ", not 15");
            }

            shapes(context, world);
        }
    }

    /**
     * The panel, beam and column, holding bricks, draw with a model: their blockstates and models are
     * written by a generator of their own, so they are checked apart from the other shapes.
     */
    private static void shapes(ClientGameTestContext context, TestSingleplayerContext world) {
        List<BlockPos> placed = world.getServer().computeOnServer(server -> {
            ServerLevel level = server.overworld();
            BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(8);
            List<BlockPos> positions = List.of(at, at.east(2), at.east(4));
            level.setBlockAndUpdate(positions.get(0), ColorizerBlocks.COLORIZER_COLUMN.get().defaultBlockState());
            level.setBlockAndUpdate(positions.get(1), ColorizerBlocks.COLORIZER_BEAM.get().defaultBlockState());
            level.setBlockAndUpdate(positions.get(2), ColorizerBlocks.COLORIZER_PANEL.get().defaultBlockState()
                    .setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true));
            positions.forEach(pos -> ((ColorizerBlockEntity) level.getBlockEntity(pos)).setStoredBlockState(Blocks.BRICKS.defaultBlockState()));
            return positions;
        });
        context.waitFor(client -> client.level != null && placed.stream().allMatch(pos -> client.level.getBlockEntity(pos) instanceof ColorizerBlockEntity colorizer
                && colorizer.getStoredBlockState().is(Blocks.BRICKS)));

        context.runOnClient(client -> {
            for (BlockPos pos : placed) {
                BlockState state = client.level.getBlockState(pos);
                Identifier particle = client.getModelManager().getBlockStateModelSet().getParticleMaterial(state, client.level, pos).sprite().contents().name();
                if (MISSING.equals(particle)) {
                    throw new AssertionError(state + " holding bricks is drawn with the missing texture");
                }
            }
        });
    }

    /** The particle sprite an item throws off, resolved exactly as a dropped item is drawn. */
    private static Identifier itemParticle(Minecraft client, ItemStack stack) {
        ItemStackRenderState state = new ItemStackRenderState();
        client.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GROUND, null, null, 0);
        Material.Baked particle = state.pickParticleMaterial(RandomSource.create(0L));
        return particle == null ? null : particle.sprite().contents().name();
    }
}
