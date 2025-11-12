package me.haedal.custom_displayname.util;

import net.minecraft.client.gui.GuiGraphics;

public final class ClientRenderUtil {
    private static volatile HeadRenderer headRenderer;

    private ClientRenderUtil() {}

    public static void setHeadRenderer(HeadRenderer renderer) {
        headRenderer = renderer;
    }

    public static void drawHead(GuiGraphics g, String playerName, int x, int y, int size) {
        HeadRenderer r = headRenderer;
        if (r != null) {
            r.draw(g, playerName, x, y, size);
        }
    }
}

