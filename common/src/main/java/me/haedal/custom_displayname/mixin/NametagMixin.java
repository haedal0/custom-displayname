package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AvatarRenderer.class)
public class NametagMixin {
    @ModifyVariable(
        method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD"), argsOnly = true
    )
    private AvatarRenderState submitNameTag(AvatarRenderState arg) {
        if (arg.nameTag != null) {
            arg.nameTag = ChatModifier.applyNicknames(arg.nameTag);
        }
        return arg;
    }
}
