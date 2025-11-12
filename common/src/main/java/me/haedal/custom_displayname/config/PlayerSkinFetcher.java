package me.haedal.custom_displayname.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.entity.player.PlayerSkin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerSkinFetcher {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static final HashMap<String, UUID> uuidCache = new HashMap<>();

    public static CompletableFuture<PlayerSkin> fetchSkinByName(String playerName) {

        return getUUIDFromNameAsync(playerName).thenCompose(uuid -> {
            if (uuid == null) return null;

            MinecraftSessionService sessionService = Minecraft.getInstance().services().sessionService();
            GameProfile filledProfile = sessionService.fetchProfile(uuid, true).profile();
            SkinManager skinManager = Minecraft.getInstance().getSkinManager();


            return skinManager.get(filledProfile)
                    .thenApply(optionalSkin -> optionalSkin.orElse(null));

        });

    }

    private static CompletableFuture<UUID> getUUIDFromNameAsync(String name) {
        if (uuidCache.containsKey(name)) return CompletableFuture.completedFuture(uuidCache.get(name));

        return CompletableFuture.supplyAsync(() -> {
            try {
                uuidCache.put(name, UUID.nameUUIDFromBytes(name.getBytes()));

                URL url = new URI("https://api.mojang.com/users/profiles/minecraft/" + name).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) {
                    uuidCache.put(name, null);
                    return null;
                }

                JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                String rawId = json.get("id").getAsString();

                UUID uuid = UUID.fromString(
                        rawId.replaceFirst(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5"
                        )
                );

                uuidCache.put(name, uuid);
                return uuid;
            } catch (Exception e) {
                uuidCache.remove(name);
                e.printStackTrace();
                return null;
            }
        }, EXECUTOR);
    }
}
