package com.grim3212.assorted.lights.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Lights' gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class LightsTestSupport {

    private LightsTestSupport() {
    }

    static JsonObject readJson(String path) {
        try (InputStream in = LightsTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = LightsTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
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
