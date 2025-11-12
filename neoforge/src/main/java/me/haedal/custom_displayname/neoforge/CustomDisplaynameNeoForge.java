package me.haedal.custom_displayname.neoforge;

import me.haedal.custom_displayname.config.ScrollablePlayerConfigScreen;
import me.haedal.custom_displayname.util.ClientRenderUtil;
import me.haedal.custom_displayname.CustomDisplayname;
import me.haedal.custom_displayname.util.HeadRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(CustomDisplayname.MOD_ID)
public final class CustomDisplaynameNeoForge {
    public CustomDisplaynameNeoForge() {
        CustomDisplayname.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (ModContainer mc, Screen parent) -> new ScrollablePlayerConfigScreen(parent)
        );
    }

    @EventBusSubscriber(modid = CustomDisplayname.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ClientRenderUtil.setHeadRenderer(new HeadRenderer());
        }
    }
}
