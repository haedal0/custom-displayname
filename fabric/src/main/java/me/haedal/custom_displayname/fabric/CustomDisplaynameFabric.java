package me.haedal.custom_displayname.fabric;

import me.haedal.custom_displayname.CustomDisplayname;
import net.fabricmc.api.ModInitializer;

public final class CustomDisplaynameFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CustomDisplayname.init();
    }
}
