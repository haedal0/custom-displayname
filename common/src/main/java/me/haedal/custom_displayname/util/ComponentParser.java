package me.haedal.custom_displayname.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentParser {
    private static final Pattern TAG_PATTERN = Pattern.compile("<([^>]+)>");

    public static MutableComponent parse(String input) {
        List<MutableComponent> parts = new ArrayList<>();
        Style currentStyle = Style.EMPTY;

        Matcher matcher = TAG_PATTERN.matcher(input);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String text = input.substring(lastEnd, matcher.start());
                if (!text.isEmpty()) {
                    parts.add(Component.literal(text).setStyle(currentStyle));
                }
            }

            String tag = matcher.group(1).toLowerCase();
            currentStyle = applyTag(currentStyle, tag);

            lastEnd = matcher.end();
        }

        if (lastEnd < input.length()) {
            String text = input.substring(lastEnd);
            if (!text.isEmpty()) {
                parts.add(Component.literal(text).setStyle(currentStyle));
            }
        }

        if (parts.isEmpty()) {
            return Component.empty();
        }

        MutableComponent result = parts.get(0);
        for (int i = 1; i < parts.size(); i++) {
            result = result.append(parts.get(i));
        }

        return result;
    }

    private static Style applyTag(Style current, String tag) {
        switch (tag) {
            case "bold":
                return current.withBold(true);
            case "italic":
                return current.withItalic(true);
            case "underlined":
                return current.withUnderlined(true);
            case "strikethrough":
                return current.withStrikethrough(true);
            case "obfuscated":
                return current.withObfuscated(true);
            case "reset":
                return Style.EMPTY;
        }

        try {
            if (tag.startsWith("#")) {
                return current.withColor(TextColor.parseColor(tag).getOrThrow());
            } else {
                ChatFormatting formatting = ChatFormatting.valueOf(tag.toUpperCase());
                if (formatting.isColor()) {
                    return current.withColor(formatting);
                }
            }
        } catch (Exception ignored) {}

        return current;
    }
}
