package me.haedal.custom_displayname.config;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ScrollablePlayerConfigScreen extends Screen {
    private final Screen parent;
    private final List<PlayerEntryWidget> widgets = new ArrayList<>();
    private double scrollOffset = 0;

    public ScrollablePlayerConfigScreen(Screen parent) {
        super(Component.literal("Player Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ModConfigPlayers.load();
        widgets.clear();

        for (int i = 0; i < ModConfigPlayers.entries.size(); i++) {
            widgets.add(new PlayerEntryWidget(i, ModConfigPlayers.entries.get(i), this::removeWidget));
        }

        addRenderableWidget(Button.builder(Component.literal("+"), b -> addWidgetGroup())
                .pos(width / 2 - 10, height - 30).size(20, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Save & Exit"), b -> {
            widgets.forEach(PlayerEntryWidget::save);
            ModConfigPlayers.save();
            minecraft.setScreen(parent);
        }).pos(width - 110, height - 30).size(100, 20).build());
    }

    @Override
    public void tick() {
        for (PlayerEntryWidget widget : this.widgets) {
            widget.tick();
        }
    }

    private void addWidgetGroup() {
        PlayerEntry e = new PlayerEntry("Player" + (widgets.size() + 1));
        ModConfigPlayers.entries.add(e);
        widgets.add(new PlayerEntryWidget(widgets.size(), e, this::removeWidget));
    }

    private void removeWidget(PlayerEntryWidget w) {
        int idx = widgets.indexOf(w);
        if (idx >= 0) {
            widgets.remove(idx);
            ModConfigPlayers.entries.remove(idx);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderTransparentBackground(g);
        g.drawCenteredString(this.font, "Player Config", width / 2, 15, 0xFFFFFF);

        int clipTop = 40;
        int clipBottom = height - 50;
        RenderSystem.enableScissorForRenderTypeDraws(0, clipTop, width, clipBottom - clipTop);

        int y = clipTop - (int) scrollOffset;
        int widgetWidth = Math.max(0, width - 40);
        for (PlayerEntryWidget w : widgets) {
            w.render(g, 20, y, widgetWidth, mouseX, mouseY, delta);
            y += PlayerEntryWidget.ROW_HEIGHT;
        }

        RenderSystem.disableScissorForRenderTypeDraws();
        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        scrollOffset = Math.max(0, Math.min(scrollOffset - g * 10, Math.max(0, widgets.size() * PlayerEntryWidget.ROW_HEIGHT - (height - 80))));
        return true;
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent mbe, boolean bl) {
        for (PlayerEntryWidget w : widgets) {
            if (w.getField1Box().mouseClicked(mbe, bl)) {
                this.setFocused(w.getField1Box());
                return true;
            }
            if (w.getField2Box().mouseClicked(mbe, bl)) {
                this.setFocused(w.getField2Box());
                return true;
            }
            if (w.getRemoveButton().mouseClicked(mbe, bl)) {
                return true;
            }
        }
        return super.mouseClicked(mbe, bl);
    }

    @Override
    public boolean keyPressed(KeyEvent ke) {
        return super.keyPressed(ke);
    }

    @Override
    public boolean charTyped(CharacterEvent ce) {
        return super.charTyped(ce);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
