package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ConfigUtil;
import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(AvatarRenderer.class)
public class NametagMixin {
    @ModifyVariable(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"), argsOnly = true)
    private AvatarRenderState submitNameTag(AvatarRenderState arg) {
        Component value = arg.nameTag;

        List<Pair<String, MutableComponent>> nicknamePairs = ConfigUtil.getConfig().getNicknamePairs();

        for (Pair<String, MutableComponent> pair : nicknamePairs) {
            if (value.getString().contains(pair.getLeft())) {
                value = ChatModifier.findAndReplace(value, pair.getLeft(), pair.getRight());
            }
        }
        arg.nameTag = value;

        return arg;
    }
}
