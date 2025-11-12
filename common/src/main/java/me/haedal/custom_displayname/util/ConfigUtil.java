package me.haedal.custom_displayname.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import me.haedal.custom_displayname.config.ModConfigPlayers;
import me.haedal.custom_displayname.config.PlayerEntry;
import net.minecraft.network.chat.MutableComponent;

public final class ConfigUtil {
    public static List<Pair<String, MutableComponent>> getNicknamePairs() {
        List<PlayerEntry> entries = ModConfigPlayers.entries;
        return entries.stream()
                .map(entry -> Pair.of(entry.field1, ComponentParser.parse(entry.field2)))
                .toList();
    }
}
