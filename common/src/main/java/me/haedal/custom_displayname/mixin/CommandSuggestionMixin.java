package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import me.haedal.custom_displayname.util.ConfigUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(CommandSuggestions.SuggestionsList.class)
public class CommandSuggestionMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void render(GuiGraphics instance, Font font, String string, int i, int j, int k) {
        List<Pair<String, MutableComponent>> nicknamePairs = ConfigUtil.getNicknamePairs();

        if (string == null) {
            instance.drawString(font, string, i, j, k);
            return;
        }

        for (Pair<String, MutableComponent> pair : nicknamePairs) {
            if (string.contains(pair.getLeft())) {
                string = ChatModifier.findAndReplace(Component.literal(string), pair.getLeft(), pair.getRight()).getString();
            }
        }

        instance.drawString(font, string, i, j, k);
    }
}
