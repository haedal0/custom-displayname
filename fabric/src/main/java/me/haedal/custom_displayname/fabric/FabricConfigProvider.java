package me.haedal.custom_displayname.fabric;

import me.haedal.custom_displayname.util.ComponentParser;
import me.haedal.custom_displayname.util.ConfigProvider;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;

public class FabricConfigProvider implements ConfigProvider {
    private final ModConfig config;

    public FabricConfigProvider() {
        this.config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public List<Pair<String, MutableComponent>> getNicknamePairs() {
        return config.nicknames.stream()
                .map(s -> {
                    String[] parts = s.split(":");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format in nicknames: " + s);
                    }
                    return Pair.of(parts[0], ComponentParser.parse(parts[1]));
                })
                .toList();
    }
}
