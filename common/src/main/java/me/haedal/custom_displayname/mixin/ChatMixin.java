package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ConfigUtil;
import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ChatComponent.class)
public class ChatMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component addMessage(Component value) {
        List<Pair<String, MutableComponent>> nicknamePairs = ConfigUtil.getNicknamePairs();

        if (value == null) return value;

        for (Pair<String, MutableComponent> pair : nicknamePairs) {
            if (value.getString().contains(pair.getLeft())) {
                value = ChatModifier.findAndReplace(value, pair.getLeft(), pair.getRight());
            }
        }

        return value;
    }
}
