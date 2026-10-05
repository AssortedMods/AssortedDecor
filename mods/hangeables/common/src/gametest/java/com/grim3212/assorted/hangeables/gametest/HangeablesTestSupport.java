package com.grim3212.assorted.hangeables.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers and constants shared by Assorted Hangeables' gametest classes, which import them
 * statically, alongside AssortedLib's {@code TestSupport}.
 */
final class HangeablesTestSupport {

    private HangeablesTestSupport() {
    }

    static final BlockPos MAIN = new BlockPos(4, 1, 4);

    static JsonObject readJson(String path) {
        try (InputStream in = HangeablesTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = HangeablesTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }
}
