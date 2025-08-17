package me.haedal.custom_displayname.util;

import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ConfigProvider {
    List<Pair<String, MutableComponent>> getNicknamePairs();
}
