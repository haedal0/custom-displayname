package me.haedal.custom_displayname.util;

import me.haedal.custom_displayname.config.PlayerSkinFetcher;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.world.entity.player.PlayerSkin;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeadRenderer {
    private final Map<String, PlayerSkin> skinCache = new HashMap<>();
    private final Map<String, CompletableFuture<PlayerSkin>> pending = new HashMap<>();

    public void draw(GuiGraphics g, String playerName, int x, int y, int size) {
        if (playerName == null || playerName.isEmpty()) {
            return;
        }

        if (skinCache.containsKey(playerName)) {
            renderHead(g, skinCache.get(playerName), x, y, size);
            return;
        }

        if (pending.containsKey(playerName)) {
            return;
        }

        CompletableFuture<PlayerSkin> skinFuture = PlayerSkinFetcher.fetchSkinByName(playerName);
        pending.put(playerName, skinFuture);

        skinFuture.thenAccept(skin -> {
            if (skin != null) {
                skinCache.put(playerName, skin);
            }
            pending.remove(playerName);
        });
    }

    

    private void renderHead(GuiGraphics g, PlayerSkin skin, int x, int y, int size) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(size / 8f, size / 8f);
        PlayerFaceRenderer.draw(g, skin, 0, 0, 8);
        g.pose().popMatrix();
    }
}
