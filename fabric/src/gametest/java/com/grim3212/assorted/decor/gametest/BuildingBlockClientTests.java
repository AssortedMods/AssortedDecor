package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.client.screen.LumberMillScreen;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.building.BeamBlock;
import com.grim3212.assorted.decor.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.decor.common.blocks.building.ColumnBlock;
import com.grim3212.assorted.decor.common.blocks.building.StoneFamily;
import com.grim3212.assorted.decor.common.blocks.building.WoodSet;
import com.grim3212.assorted.decor.common.crafting.DecorRecipeTypes;
import com.grim3212.assorted.decor.common.inventory.LumberMillMenu;
import com.grim3212.assorted.decor.compat.jei.JEIAssortedDecor;
import com.grim3212.assorted.lib.crafting.SyncedRecipes;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Every building block placed and drawn: none may break into the missing texture. The screenshots
 * are the stones, the woods, and the blocks that shape themselves as they are built.
 */
final class BuildingBlockClientTests {

    private static final Identifier MISSING = Identifier.withDefaultNamespace("missingno");

    private BuildingBlockClientTests() {
    }

    static void run(ClientGameTestContext context, TestSingleplayerContext world) {
        BlockPos origin = world.getServer().computeOnServer(server -> {
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            // Held in the air between shots: they are taken from twenty blocks up.
            player.setNoGravity(true);
            return player.blockPosition().offset(0, 20, 0);
        });

        // Every block, in rows well out of shot, for the texture check.
        List<BlockPos> everything = world.getServer().computeOnServer(server -> {
            ServerLevel level = server.overworld();
            List<BlockPos> positions = new ArrayList<>();
            List<IRegistryObject<? extends Block>> all = BuildingBlocks.all();
            for (int i = 0; i < all.size(); i++) {
                BlockPos at = origin.offset(40 + (i % 32) * 2, 0, (i / 32) * 2);
                level.setBlockAndUpdate(at, all.get(i).get().defaultBlockState());
                positions.add(at);
            }
            return positions;
        });
        context.waitFor(client -> client.level != null && everything.stream().allMatch(pos -> !client.level.getBlockState(pos).isAir()));
        context.runOnClient(client -> {
            List<String> missing = new ArrayList<>();
            for (BlockPos pos : everything) {
                BlockState state = client.level.getBlockState(pos);
                Identifier particle = client.getModelManager().getBlockStateModelSet().getParticleMaterial(state, client.level, pos).sprite().contents().name();
                if (MISSING.equals(particle)) {
                    missing.add(state.toString());
                }
            }
            if (!missing.isEmpty()) {
                throw new AssertionError("building blocks drawn with the missing texture: " + missing);
            }
        });

        shoot(context, world, origin, "assorteddecor_building_stones", level -> {
            List<StoneFamily> stones = BuildingBlocks.stones();
            for (int row = 0; row < stones.size(); row++) {
                List<Block> blocks = BuildingBlocks.blocksOf(stones.get(row)).stream()
                        .filter(b -> !(b instanceof ColumnBlock) && b.defaultBlockState().isCollisionShapeFullBlock(level, BlockPos.ZERO)).toList();
                for (int i = 0; i < blocks.size() && i < 20; i++) {
                    level.setBlockAndUpdate(origin.offset(i - 10, 13 - row, 14), blocks.get(i).defaultBlockState());
                }
            }
        }, 0.0F, -25.0F);

        shoot(context, world, origin, "assorteddecor_building_woods", level -> {
            List<WoodSet> woods = BuildingBlocks.woods();
            for (int row = 0; row < woods.size(); row++) {
                List<Block> blocks = BuildingBlocks.blocksOf(woods.get(row)).stream()
                        .filter(b -> b.defaultBlockState().isCollisionShapeFullBlock(level, BlockPos.ZERO)).toList();
                for (int i = 0; i < blocks.size(); i++) {
                    level.setBlockAndUpdate(origin.offset(3 - i, 13 - row, -14), blocks.get(i).defaultBlockState());
                }
            }
        }, 180.0F, -25.0F);

        shoot(context, world, origin.offset(0, 0, -2), "assorteddecor_building_shapes", level -> {
            BlockPos floor = origin.offset(-6, -3, -12);
            for (int x = -1; x < 14; x++) {
                for (int z = -1; z < 8; z++) {
                    level.setBlockAndUpdate(floor.offset(x, 0, z), Blocks.SMOOTH_STONE.defaultBlockState());
                }
            }
            BlockPos ground = floor.above();
            // Columns stood up in threes, a single one, and three laid on their side.
            for (int y = 0; y < 3; y++) {
                placeColumn(level, ground.offset(0, y, 2), BuildingBlocks.STONE, Direction.Axis.Y);
                placeColumn(level, ground.offset(2, y, 2), BuildingBlocks.DEEPSLATE, Direction.Axis.Y);
                placeColumn(level, ground.offset(4, y, 2), BuildingBlocks.SANDSTONE, Direction.Axis.Y);
            }
            placeColumn(level, ground.offset(1, 0, 6), BuildingBlocks.GRANITE, Direction.Axis.Y);
            for (int x = 3; x < 6; x++) {
                placeColumn(level, ground.offset(x, 0, 6), BuildingBlocks.CALCITE, Direction.Axis.X);
            }
            // A crossing of oak beams and a tee of iron ones.
            Block beam = BuildingBlocks.OAK.beam().get();
            BlockPos cross = ground.offset(8, 2, 3);
            placeBeam(level, cross.north(), beam, Direction.Axis.Z);
            placeBeam(level, cross, beam, Direction.Axis.Z);
            placeBeam(level, cross.south(), beam, Direction.Axis.Z);
            placeBeam(level, cross.west(), beam, Direction.Axis.X);
            placeBeam(level, cross.east(), beam, Direction.Axis.X);
            BlockPos tee = ground.offset(11, 2, 5);
            placeBeam(level, tee, BuildingBlocks.IRON_BEAM.get(), Direction.Axis.Z);
            placeBeam(level, tee.south(), BuildingBlocks.IRON_BEAM.get(), Direction.Axis.Z);
            placeBeam(level, tee.west(), BuildingBlocks.IRON_BEAM.get(), Direction.Axis.X);
            // A corner of a panelled room: walls, floor and ceiling.
            Block panel = BuildingBlocks.CHERRY.panel().get();
            BlockPos room = ground.offset(9, 0, 0);
            for (int y = 0; y < 2; y++) {
                for (int i = 0; i < 3; i++) {
                    level.setBlockAndUpdate(room.offset(i, y, 0), panel(panel, i == 0, y == 0, y == 1));
                    level.setBlockAndUpdate(room.offset(0, y, i), panel(panel, true, y == 0, y == 1).setValue(MultifaceBlock.getFaceProperty(Direction.NORTH), i == 0));
                }
            }
            level.setBlockAndUpdate(ground.offset(7, 0, 6), BuildingBlocks.MEAT_BLOCK.get().defaultBlockState());
            level.setBlockAndUpdate(ground.offset(12, 0, 2), DecorBlocks.LUMBER_MILL.get().defaultBlockState());
            // The colorizer shapes, holding bricks.
            for (int y = 0; y < 2; y++) {
                placeColumn(level, ground.offset(6, y, 0), DecorBlocks.COLORIZER_COLUMN.get(), Direction.Axis.Y);
            }
            placeBeam(level, ground.offset(6, 2, 1), DecorBlocks.COLORIZER_BEAM.get(), Direction.Axis.Z);
            level.setBlockAndUpdate(ground.offset(7, 0, 0), DecorBlocks.COLORIZER_PANEL.get().defaultBlockState()
                    .setValue(MultifaceBlock.getFaceProperty(Direction.NORTH), true).setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true));
            for (BlockPos pos : List.of(ground.offset(6, 0, 0), ground.offset(6, 1, 0), ground.offset(6, 2, 1), ground.offset(7, 0, 0))) {
                ((ColorizerBlockEntity) level.getBlockEntity(pos)).setStoredBlockState(Blocks.BRICKS.defaultBlockState());
            }
        }, 180.0F, 35.0F);

        // The lumber mill's screen, oak planks in, every oak block on offer.
        BlockPos lumberMill = origin.offset(-6 + 12, -2, -12 + 2);
        world.getServer().runOnServer(server -> {
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            BlockState state = server.overworld().getBlockState(lumberMill);
            // Within reach, or the menu closes as soon as it opens.
            player.connection.teleport(lumberMill.getX() + 0.5D, lumberMill.getY() + 1.0D, lumberMill.getZ() + 2.5D, 180.0F, 40.0F);
            player.openMenu(state.getMenuProvider(server.overworld(), lumberMill));
            ((LumberMillMenu) player.containerMenu).container.setItem(0, new ItemStack(Blocks.OAK_PLANKS, 16));
        });
        context.waitFor(client -> client.gui.screen() instanceof LumberMillScreen);
        context.waitTicks(10);
        context.runOnClient(client -> {
            LumberMillMenu menu = ((LumberMillScreen) client.gui.screen()).getMenu();
            if (menu.getNumberOfVisibleRecipes() == 0) {
                throw new AssertionError("the lumber mill offers nothing for " + menu.container.getItem(0) + "; the client was sent "
                        + SyncedRecipes.byType(DecorRecipeTypes.LUMBER_MILL.get()).size() + " lumber mill recipes");
            }
        });
        context.takeScreenshot("assorteddecor_lumber_mill");
        context.runOnClient(client -> client.player.closeContainer());

        // JEI lists the same recipes on its lumber mill page, read from what the server sent.
        context.waitFor(client -> JEIAssortedDecor.runtime() != null
                && JEIAssortedDecor.runtime().getRecipeManager().createRecipeLookup(JEIAssortedDecor.LUMBER_MILL).get().count() > 100);
        context.runOnClient(client -> JEIAssortedDecor.runtime().getRecipesGui().showTypes(List.of(JEIAssortedDecor.LUMBER_MILL)));
        context.waitTicks(10);
        context.takeScreenshot("assorteddecor_lumber_mill_jei");
        context.setScreen(() -> null);
    }

    private static void shoot(ClientGameTestContext context, TestSingleplayerContext world, BlockPos eye, String name, Consumer<ServerLevel> build, float yRot, float xRot) {
        world.getServer().runOnServer(server -> {
            build.accept(server.overworld());
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            player.connection.teleport(eye.getX() + 0.5D, eye.getY(), eye.getZ() + 0.5D, yRot, xRot);
            player.setDeltaMovement(0, 0, 0);
        });
        context.waitTicks(30);
        context.takeScreenshot(name);
    }

    private static void placeColumn(ServerLevel level, BlockPos pos, StoneFamily family, Direction.Axis axis) {
        placeColumn(level, pos, family.column().get(), axis);
    }

    private static void placeColumn(ServerLevel level, BlockPos pos, Block column, Direction.Axis axis) {
        BlockState state = column.defaultBlockState().setValue(ColumnBlock.AXIS, axis);
        level.setBlockAndUpdate(pos, Block.updateFromNeighbourShapes(state, level, pos));
    }

    private static void placeBeam(ServerLevel level, BlockPos pos, Block beam, Direction.Axis axis) {
        BlockState state = beam.defaultBlockState().setValue(BeamBlock.AXIS, axis).setValue(BeamBlock.HALF, Half.TOP);
        level.setBlockAndUpdate(pos, Block.updateFromNeighbourShapes(state, level, pos));
    }

    /** A room's north wall, with the west wall too where {@code corner}. */
    private static BlockState panel(Block panel, boolean corner, boolean floor, boolean ceiling) {
        return panel.defaultBlockState().setValue(MultifaceBlock.getFaceProperty(Direction.NORTH), true)
                .setValue(MultifaceBlock.getFaceProperty(Direction.WEST), corner)
                .setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), floor)
                .setValue(MultifaceBlock.getFaceProperty(Direction.UP), ceiling);
    }
}
