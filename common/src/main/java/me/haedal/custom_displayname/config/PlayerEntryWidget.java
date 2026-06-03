package me.haedal.custom_displayname.config;

import me.haedal.custom_displayname.util.ClientRenderUtil;
import me.haedal.custom_displayname.util.ComponentParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class PlayerEntryWidget extends AbstractWidget {
    private static final int DEFAULT_WIDGET_WIDTH = 380;
    private static final int HEAD_SIZE = 20;
    private static final int HEAD_MARGIN_LEFT = 6;
    private static final int FIELD_MAX_WIDTH = 140;
    private static final int FIELD_MIN_WIDTH = 60;
    private static final int FIELD_ABSOLUTE_MIN_WIDTH = 40;
    private static final int FIELD_GAP = 8;
    private static final int FIELD_HEIGHT = 20;
    private static final int CONTROL_TOP_PADDING = 4;
    private static final int PREVIEW_PADDING = 6;
    private static final int REMOVE_BUTTON_MARGIN = 10;

    public static final int ROW_HEIGHT = 40;

    private final Minecraft mc = Minecraft.getInstance();
    private final PlayerEntry entry;
    private final EditBox nameBox;
    private final EditBox displayBox;
    private Component preview;
    private final Button removeButton;

    private long lastTypedTime = 0;
    private static final long DEBOUNCE_DELAY = 500;
    private String headName;

    public PlayerEntryWidget(int index, PlayerEntry entry, Consumer<PlayerEntryWidget> onRemove) {
        super(0, index * ROW_HEIGHT, DEFAULT_WIDGET_WIDTH, ROW_HEIGHT, Component.literal("PlayerEntry"));
        this.entry = entry;

        this.nameBox    = new EditBox(mc.font, 0, 0, FIELD_MAX_WIDTH, FIELD_HEIGHT, Component.translatable("custom_displayname.config.col.player"));
        this.displayBox = new EditBox(mc.font, 0, 0, FIELD_MAX_WIDTH, FIELD_HEIGHT, Component.translatable("custom_displayname.config.col.display"));
        this.nameBox.setValue(entry.playerName);
        this.displayBox.setValue(entry.displayName);
        this.preview = ComponentParser.parse(displayBox.getValue());
        this.headName = entry.playerName;
        this.nameBox.setResponder(s -> this.lastTypedTime = System.currentTimeMillis());
        this.displayBox.setResponder(s -> this.preview = ComponentParser.parse(s));

        this.removeButton = Button.builder(Component.literal("-"), b -> onRemove.accept(this))
                .pos(300, 0).size(20, 20).build();
    }

    public void render(GuiGraphicsExtractor g, int x, int y, int width, int mouseX, int mouseY, float delta) {
        this.setX(x);
        this.setY(y);
        this.setWidth(Math.max(0, width));
        this.extractWidgetRenderState(g, mouseX, mouseY, delta);
    }

    public EditBox getNameBox()    { return nameBox; }
    public EditBox getDisplayBox() { return displayBox; }
    public Button  getRemoveButton() { return removeButton; }

    public void save() {
        entry.playerName  = nameBox.getValue();
        entry.displayName = displayBox.getValue();
    }

    public void tick() {
        if (System.currentTimeMillis() - lastTypedTime > DEBOUNCE_DELAY) {
            String current = nameBox.getValue();
            if (!current.equals(headName)) headName = current;
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        positionControls();
        drawPreview(g);
        ClientRenderUtil.drawHead(g, headName, headX(), headY(), HEAD_SIZE);
        nameBox.extractRenderState(g, mouseX, mouseY, partialTick);
        displayBox.extractRenderState(g, mouseX, mouseY, partialTick);
        removeButton.extractRenderState(g, mouseX, mouseY, partialTick);

        if (mouseX >= displayBox.getX() && mouseX <= displayBox.getX() + displayBox.getWidth()
                && mouseY >= displayBox.getY() && mouseY <= displayBox.getY() + FIELD_HEIGHT) {
            g.setTooltipForNextFrame(mc.font, Component.translatable("custom_displayname.config.hint"), mouseX, mouseY);
        }
    }

    private void positionControls() {
        int controlY  = getY() + CONTROL_TOP_PADDING;
        int rightEdge = getX() + Math.max(0, getWidth());

        nameBox.setY(controlY);
        displayBox.setY(controlY);
        removeButton.setY(controlY);

        int removeX = rightEdge - removeButton.getWidth() - REMOVE_BUTTON_MARGIN;
        removeButton.setX(removeX);

        int baseX         = getX() + HEAD_MARGIN_LEFT + HEAD_SIZE + FIELD_GAP;
        int maxFieldRight = removeX - PREVIEW_PADDING;
        int available     = Math.max(0, maxFieldRight - baseX);

        int fieldWidth = clampFieldWidth(available);

        int nameX    = baseX;
        int displayX = nameX + fieldWidth + FIELD_GAP;
        if (displayX + fieldWidth > maxFieldRight) {
            displayX = Math.max(nameX, maxFieldRight - fieldWidth);
            nameX    = Math.max(baseX, displayX - fieldWidth - FIELD_GAP);
        }

        nameBox.setX(nameX);    nameBox.setWidth(fieldWidth);
        displayBox.setX(displayX); displayBox.setWidth(fieldWidth);
    }

    private int clampFieldWidth(int available) {
        int w = available > 0 ? (available - FIELD_GAP) / 2 : FIELD_MIN_WIDTH;
        w = Math.max(FIELD_MIN_WIDTH, Math.min(FIELD_MAX_WIDTH, w));
        if (w * 2 + FIELD_GAP > available) w = Math.max(FIELD_ABSOLUTE_MIN_WIDTH, (available - FIELD_GAP) / 2);
        return Math.max(FIELD_ABSOLUTE_MIN_WIDTH, w);
    }

    private void drawPreview(GuiGraphicsExtractor g) {
        int controlY     = getY() + CONTROL_TOP_PADDING;
        int removeX      = getX() + Math.max(0, getWidth()) - removeButton.getWidth() - REMOVE_BUTTON_MARGIN;
        int previewLeft  = displayBox.getX() + displayBox.getWidth() + PREVIEW_PADDING;
        int previewRight = removeX - PREVIEW_PADDING;
        int previewWidth = Math.max(0, previewRight - previewLeft);
        if (previewWidth <= 0) return;

        Component text = this.preview != null ? this.preview : Component.empty();
        if (text.getString().isEmpty()) return;

        int previewY = controlY + (FIELD_HEIGHT - mc.font.lineHeight) / 2;
        g.textWithWordWrap(mc.font, text, previewLeft, previewY, previewWidth, 0xFFFFFFFF);
    }

    private int headX() { return getX() + HEAD_MARGIN_LEFT; }
    private int headY() { return getY() + CONTROL_TOP_PADDING + Math.max(0, (FIELD_HEIGHT - HEAD_SIZE) / 2); }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
