package me.haedal.custom_displayname.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ScrollablePlayerConfigScreen extends Screen {

    private static final int HEADER_Y            = 8;
    private static final int COLUMN_HEADER_Y     = 30;
    private static final int CONTENT_TOP         = 44;
    private static final int CONTENT_BOTTOM_MARGIN = 36;
    private static final int BUTTON_Y_FROM_BOTTOM  = 22;

    private final Screen parent;
    private final List<PlayerEntryWidget> widgets = new ArrayList<>();
    private double scrollOffset = 0;

    private Component errorMessage = null;
    private long errorExpiry = 0;

    public ScrollablePlayerConfigScreen(Screen parent) {
        super(Component.translatable("custom_displayname.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ModConfigPlayers.load();
        widgets.clear();
        scrollOffset = 0;

        for (int i = 0; i < ModConfigPlayers.entries.size(); i++) {
            widgets.add(new PlayerEntryWidget(i, ModConfigPlayers.entries.get(i), this::removeEntry));
        }

        addRenderableWidget(Button.builder(
                Component.translatable("custom_displayname.config.add"),
                b -> addEntry()
        ).pos(10, height - BUTTON_Y_FROM_BOTTOM - 2).size(80, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("custom_displayname.config.cancel"),
                b -> minecraft.setScreen(parent)
        ).pos(width - 220, height - BUTTON_Y_FROM_BOTTOM - 2).size(100, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("custom_displayname.config.save"),
                b -> trySave()
        ).pos(width - 110, height - BUTTON_Y_FROM_BOTTOM - 2).size(100, 20).build());
    }

    private void trySave() {
        boolean anyEmpty = widgets.stream().anyMatch(w -> w.getNameBox().getValue().isBlank());
        if (anyEmpty) {
            errorMessage = Component.translatable("custom_displayname.config.error.empty");
            errorExpiry  = System.currentTimeMillis() + 3000;
            return;
        }
        widgets.forEach(PlayerEntryWidget::save);
        ModConfigPlayers.save();
        minecraft.setScreen(parent);
    }

    private void addEntry() {
        PlayerEntry e = new PlayerEntry("Player" + (widgets.size() + 1));
        ModConfigPlayers.entries.add(e);
        widgets.add(new PlayerEntryWidget(widgets.size() - 1, e, this::removeEntry));
        errorMessage = null;
    }

    private void removeEntry(PlayerEntryWidget w) {
        int idx = widgets.indexOf(w);
        if (idx >= 0) {
            widgets.remove(idx);
            ModConfigPlayers.entries.remove(idx);
        }
        errorMessage = null;
    }

    @Override
    public void tick() {
        widgets.forEach(PlayerEntryWidget::tick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        extractTransparentBackground(g);
        g.centeredText(this.font, this.title, width / 2, HEADER_Y, 0xFFFFFF);
        drawColumnHeaders(g);

        int clipBottom = height - CONTENT_BOTTOM_MARGIN;
        g.enableScissor(0, CONTENT_TOP, width, clipBottom);

        if (widgets.isEmpty()) {
            int emptyY = CONTENT_TOP + (clipBottom - CONTENT_TOP) / 2 - font.lineHeight / 2;
            g.centeredText(font, Component.translatable("custom_displayname.config.empty"), width / 2, emptyY, 0xAAAAAA);
        } else {
            int y = CONTENT_TOP - (int) scrollOffset;
            int widgetWidth = Math.max(0, width - 40);
            for (PlayerEntryWidget w : widgets) {
                w.render(g, 20, y, widgetWidth, mouseX, mouseY, delta);
                y += PlayerEntryWidget.ROW_HEIGHT;
            }
        }

        g.disableScissor();

        if (errorMessage != null && System.currentTimeMillis() < errorExpiry) {
            g.centeredText(font, errorMessage, width / 2, height - BUTTON_Y_FROM_BOTTOM - 18, 0xFF5555);
        } else {
            errorMessage = null;
        }

        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    private void drawColumnHeaders(GuiGraphicsExtractor g) {
        int left       = 20;
        int afterHead  = left + 6 + 20 + 8;
        int widgetWidth   = Math.max(0, width - 40);
        int removeWidth   = 20 + 10;
        int maxFieldRight = left + widgetWidth - removeWidth - 6;
        int available  = Math.max(0, maxFieldRight - afterHead);
        int fieldWidth = available > 0 ? Math.min(140, Math.max(40, (available - 8) / 2)) : 60;

        int nameX    = afterHead;
        int displayX = nameX + fieldWidth + 8;
        int previewX = displayX + fieldWidth + 6;

        g.text(font, Component.translatable("custom_displayname.config.col.player"),  nameX,    COLUMN_HEADER_Y, 0xAAAAAA);
        g.text(font, Component.translatable("custom_displayname.config.col.display"), displayX, COLUMN_HEADER_Y, 0xAAAAAA);
        if (previewX < left + widgetWidth - removeWidth) {
            g.text(font, Component.translatable("custom_displayname.config.col.preview"), previewX, COLUMN_HEADER_Y, 0xAAAAAA);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double hDelta, double vDelta) {
        int maxScroll = Math.max(0, widgets.size() * PlayerEntryWidget.ROW_HEIGHT - (height - CONTENT_TOP - CONTENT_BOTTOM_MARGIN));
        scrollOffset = Math.max(0, Math.min(scrollOffset - vDelta * 10, maxScroll));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mbe, boolean bl) {
        for (PlayerEntryWidget w : widgets) {
            if (w.getNameBox().mouseClicked(mbe, bl))    { this.setFocused(w.getNameBox());    return true; }
            if (w.getDisplayBox().mouseClicked(mbe, bl)) { this.setFocused(w.getDisplayBox()); return true; }
            if (w.getRemoveButton().mouseClicked(mbe, bl)) return true;
        }
        return super.mouseClicked(mbe, bl);
    }

    @Override public boolean keyPressed(KeyEvent ke)      { return super.keyPressed(ke); }
    @Override public boolean charTyped(CharacterEvent ce) { return super.charTyped(ce); }
    @Override public boolean isPauseScreen()              { return false; }
}
