package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class ServerSelectionMixin {
    @Shadow @Nullable private List<Component> onlinePlayersTooltip;

    @Inject(
        method = "extractContent",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Ljava/util/List;II)V")
    )
    private void applyNicknamesToTooltip(GuiGraphicsExtractor g, int i, int j, boolean bl, float f, CallbackInfo ci) {
        if (onlinePlayersTooltip == null) return;
        onlinePlayersTooltip = onlinePlayersTooltip.stream()
                .map(c -> c != null ? ChatModifier.applyNicknames(c) : null)
                .collect(Collectors.toList());
    }
}
