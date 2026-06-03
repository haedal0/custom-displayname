package me.haedal.custom_displayname.util;

import me.haedal.custom_displayname.config.ModConfigPlayers;
import me.haedal.custom_displayname.config.PlayerEntry;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class ConfigUtil {
    private static volatile List<Pair<String, MutableComponent>> cache = null;

    private ConfigUtil() {}

    public static List<Pair<String, MutableComponent>> getNicknamePairs() {
        List<Pair<String, MutableComponent>> result = cache;
        if (result == null) {
            result = ModConfigPlayers.entries.stream()
                    .filter(e -> e.playerName != null && !e.playerName.isEmpty())
                    .map(entry -> Pair.<String, MutableComponent>of(entry.playerName, ComponentParser.parse(entry.displayName)))
                    .toList();
            cache = result;
        }
        return result;
    }

    public static void invalidate() {
        cache = null;
    }
}
