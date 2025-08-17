package me.haedal.custom_displayname.util;

import me.haedal.custom_displayname.CustomDisplayname;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ChatModifier {
    public static Component findAndReplace(Component component, String targetStr, Component replacement) {
        MutableComponent modifiedComponent = findAndReplaceInSingleComponent(component.plainCopy().withStyle(component.getStyle()), targetStr, replacement);

        for (Component sibling : component.getSiblings()) {
            modifiedComponent.append(findAndReplace(sibling, targetStr, replacement));
        }

        return modifiedComponent;
    }

    private static MutableComponent findAndReplaceInSingleComponent(Component component, String targetStr, Component replacement) {
        String text = component.getString();

        if (!text.contains(targetStr)) {
            return component.copy();
        }

        if (text.equals(targetStr)) {
            return replacement.copy().withStyle(component.getStyle());
        }

        MutableComponent newComponent = Component.literal("");

        String[] parts = text.split(targetStr);

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                newComponent.append(Component.literal(parts[i]).withStyle(component.getStyle()));
            }

            if (i < parts.length - 1) {
                newComponent.append(replacement.copy().withStyle(replacement.getStyle().isEmpty() ? component.getStyle() : replacement.getStyle()));
            }
        }

        return newComponent;
    }
}