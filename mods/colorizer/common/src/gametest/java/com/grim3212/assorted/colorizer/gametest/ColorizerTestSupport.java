package com.grim3212.assorted.colorizer.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerLampPost;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers, constants and fixtures shared by Assorted Colorizer's gametest classes, which import them
 * statically, alongside AssortedLib's {@code TestSupport}.
 */
final class ColorizerTestSupport {

    private ColorizerTestSupport() {
    }

    static final BlockPos MAIN = new BlockPos(4, 1, 4);

    static JsonObject readJson(String path) {
        try (InputStream in = ColorizerTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = ColorizerTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Puts a colorizer down ready to be filled. A door needs both halves and a lamp post all three
     * parts before it takes a block, and the side-attached shapes need a wall to hang on.
     */
    static void placeShape(GameTestHelper helper, BlockPos rel, BlockState state) {
        if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
            state = state.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
        }

        if (state.hasProperty(DoorBlock.HALF)) {
            helper.setBlock(rel, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            helper.setBlock(rel.above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        } else if (state.hasProperty(ColorizerLampPost.PART)) {
            helper.setBlock(rel, state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.BOTTOM));
            helper.setBlock(rel.above(), state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.MIDDLE));
            helper.setBlock(rel.above(2), state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.TOP));
        } else {
            helper.setBlock(rel, state);
        }
    }

    /**
     * The light the block at {@code rel} declares for its own state and position. Tests assert this
     * rather than the light engine's brightness, which light from concurrently running neighbouring
     * test boxes raises.
     */
    static int lightEmission(GameTestHelper helper, BlockPos rel) {
        return helper.getLevel().getLightEmission(helper.absolutePos(rel));
    }
}
