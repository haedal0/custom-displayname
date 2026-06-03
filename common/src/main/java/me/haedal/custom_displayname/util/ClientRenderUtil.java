package me.haedal.custom_displayname.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ClientRenderUtil {
    private static volatile HeadRenderer headRenderer;

    private ClientRenderUtil() {}

    public static void setHeadRenderer(HeadRenderer renderer) {
        headRenderer = renderer;
    }

    public static void drawHead(GuiGraphicsExtractor g, String playerName, int x, int y, int size) {
        HeadRenderer r = headRenderer;
        if (r != null) {
            r.draw(g, playerName, x, y, size);
        }
    }
}
