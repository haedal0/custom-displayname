package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntry.class)
public class SocialInteractionsMixin {
    @ModifyArg(
        method = "extractContent",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"),
        index = 1
    )
    private String modifyPlayerName(String name) {
        if (name == null) return null;
        return ChatModifier.applyNicknames(Component.literal(name)).getString();
    }
}
