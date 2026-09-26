package com.grim3212.assorted.gates.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Gates' gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class GatesTestSupport {

    private GatesTestSupport() {
    }

    static JsonObject readJson(String path) {
        try (InputStream in = GatesTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = GatesTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }
}
