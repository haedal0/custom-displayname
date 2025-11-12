package me.haedal.custom_displayname;

import me.haedal.custom_displayname.config.ModConfigPlayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CustomDisplayname {
    public static final String MOD_ID = "custom_displayname";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        ModConfigPlayers.load();
    }
}
