package me.haedal.custom_displayname.mixin;

import me.haedal.custom_displayname.util.ChatModifier;
import me.haedal.custom_displayname.util.ConfigUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class ServerSelectionMixin {
    @Shadow @Nullable private List<Component> onlinePlayersTooltip;

    @Inject(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Ljava/util/List;II)V"))
    private void render(GuiGraphics guiGraphics, int i, int j, boolean bl, float f, CallbackInfo ci) {
        List<Pair<String, MutableComponent>> nicknamePairs = ConfigUtil.getNicknamePairs();

        List<Component> componentList = this.onlinePlayersTooltip;
        if (componentList == null) return;

        List<String> stringList = componentList.stream().map(Component::getString).toList();

        for (Pair<String, MutableComponent> pair : nicknamePairs) {
            if (stringList.contains(pair.getLeft())) {
                int idx = stringList.indexOf(pair.getLeft());
                Component component = componentList.get(idx);
                componentList.remove(idx);

                componentList.add(idx, ChatModifier.findAndReplace(component, pair.getLeft(), pair.getRight()));
            }
        }

        this.onlinePlayersTooltip = componentList;
    }
}
