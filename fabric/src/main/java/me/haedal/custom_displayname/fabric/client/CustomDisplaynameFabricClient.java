package me.haedal.custom_displayname.fabric.client;

import me.haedal.custom_displayname.util.ClientRenderUtil;
import me.haedal.custom_displayname.util.HeadRenderer;
import net.fabricmc.api.ClientModInitializer;

public final class CustomDisplaynameFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRenderUtil.setHeadRenderer(new HeadRenderer());
    }
}
