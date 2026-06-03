package me.haedal.custom_displayname.util;

import me.haedal.custom_displayname.util.ConfigUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatModifier {

    public static Component applyNicknames(Component value) {
        if (value == null) return null;
        for (var pair : ConfigUtil.getNicknamePairs()) {
            if (value.getString().contains(pair.getLeft())) {
                value = findAndReplace(value, pair.getLeft(), pair.getRight());
            }
        }
        return value;
    }

    public static Component findAndReplace(Component component, String targetStr, Component replacement) {
        if (component == null) {
            return null;
        }

        List<Component> parts = new ArrayList<>();
        parts.add(processComponent(component, targetStr, replacement));

        for (Component sibling : component.getSiblings()) {
            parts.add(findAndReplace(sibling, targetStr, replacement));
        }

        MutableComponent result = Component.empty();
        for (Component part : parts) {
            result.append(part);
        }
        return result;
    }

    private static Component processComponent(Component component, String targetStr, Component replacement) {
        Object contents = component.getContents();

        if (contents instanceof TranslatableContents translatableContents) {
            Object[] originalArgs = translatableContents.getArgs();
            Object[] newArgs = new Object[originalArgs.length];

            for (int i = 0; i < originalArgs.length; i++) {
                if (originalArgs[i] instanceof Component argComponent) {
                    newArgs[i] = findAndReplace(argComponent, targetStr, replacement);
                } else {
                    newArgs[i] = originalArgs[i];
                }
            }

            MutableComponent newTranslatable = Component.translatable(translatableContents.getKey(), newArgs);
            newTranslatable.withStyle(component.getStyle());
            return newTranslatable;
        } else {
            MutableComponent siblinglessCopy = component.copy();
            siblinglessCopy.getSiblings().clear();

            Optional<Component> result = siblinglessCopy.visit((style, text) -> {
                if (text.isEmpty() || !text.contains(targetStr)) {
                    return Optional.of(Component.literal(text).withStyle(style));
                }

                MutableComponent newComponent = Component.empty();
                int lastIndex = 0;
                int findIndex;

                while ((findIndex = text.indexOf(targetStr, lastIndex)) != -1) {
                    if (findIndex > lastIndex) {
                        newComponent.append(Component.literal(text.substring(lastIndex, findIndex)).withStyle(style));
                    }
                    MutableComponent styledReplacement = replacement.copy();
                    if (replacement.getStyle().isEmpty()) {
                        styledReplacement.setStyle(style);
                    }
                    newComponent.append(styledReplacement);
                    lastIndex = findIndex + targetStr.length();
                }

                if (lastIndex < text.length()) {
                    newComponent.append(Component.literal(text.substring(lastIndex)).withStyle(style));
                }

                return Optional.of(newComponent);
            }, siblinglessCopy.getStyle());

            return result.orElse(siblinglessCopy);
        }
    }
}