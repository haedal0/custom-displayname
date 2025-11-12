package me.haedal.custom_displayname.config;

import me.haedal.custom_displayname.CustomDisplayname;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfigPlayers {
    private static final Path PATH = Path.of("config/custom_displayname.json");
    private static final Gson GSON = new Gson();

    public static List<PlayerEntry> entries = new ArrayList<>();

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                try (Reader r = Files.newBufferedReader(PATH)) {
                    Type listType = new TypeToken<List<PlayerEntry>>(){}.getType();
                    entries = GSON.fromJson(r, listType);
                }
            } else {
                save();
            }
        } catch (IOException e) {
            CustomDisplayname.LOGGER.error("Failed to load custom displayname config", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer w = Files.newBufferedWriter(PATH)) {
                GSON.toJson(entries, w);
            }
        } catch (IOException e) {
            CustomDisplayname.LOGGER.error("Failed to save custom displayname config", e);
        }
    }
}
