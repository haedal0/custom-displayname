package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class TablistMixin {
    @ModifyVariable(method = "decorateName", at = @At("HEAD"), argsOnly = true)
    private MutableComponent decorateName(MutableComponent value) {
        Component result = ChatModifier.applyNicknames(value);
        return result instanceof MutableComponent mc ? mc : result.copy();
    }
}
