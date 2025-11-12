package me.haedal.custom_displayname.config;

import me.haedal.custom_displayname.util.ClientRenderUtil;
import me.haedal.custom_displayname.util.ComponentParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private static final int PREVIEW_PADDING = 6;
    private static final int REMOVE_BUTTON_MARGIN = 10;

    public static final int ROW_HEIGHT = 40;

    private final Minecraft mc = Minecraft.getInstance();
    private final PlayerEntry entry;
    private final EditBox field1Box;
    private final EditBox field2Box;
    private Component preview;
    private final Button removeButton;

    private long lastTypedTime = 0;
    private static final long DEBOUNCE_DELAY = 500;
    private String nameToRender;


    public PlayerEntryWidget(int index, PlayerEntry entry, Consumer<PlayerEntryWidget> onRemove) {
        super(0, index * ROW_HEIGHT, DEFAULT_WIDGET_WIDTH, ROW_HEIGHT, Component.literal("PlayerEntry"));

        this.entry = entry;

        this.field1Box = new EditBox(mc.font, 0, 0, FIELD_MAX_WIDTH, FIELD_HEIGHT, Component.literal("Field1"));
        this.field2Box = new EditBox(mc.font, 0, 0, FIELD_MAX_WIDTH, FIELD_HEIGHT, Component.literal("Field2"));
        this.field1Box.setValue(entry.field1);
        this.field2Box.setValue(entry.field2);
        this.preview = ComponentParser.parse(field2Box.getValue());
        this.nameToRender = entry.field1;
        this.field1Box.setResponder(s -> this.lastTypedTime = System.currentTimeMillis());
        this.field2Box.setResponder(s -> this.preview = ComponentParser.parse(s));

        this.removeButton = Button.builder(Component.literal("-"), b -> onRemove.accept(this))
                .pos(300, 0).size(20, 20).build();
    }


    public void render(GuiGraphics g, int x, int y, int width, int mouseX, int mouseY, float delta) {
        this.setX(x);
        this.setY(y);
        this.setWidth(Math.max(0, width));
        this.renderWidget(g, mouseX, mouseY, delta);
    }

    public EditBox getField1Box() {
        return field1Box;
    }

    public EditBox getField2Box() {
        return field2Box;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public void save() {
        entry.field1 = field1Box.getValue();
        entry.field2 = field2Box.getValue();
    }

    public void tick() {
        if (System.currentTimeMillis() - lastTypedTime > DEBOUNCE_DELAY) {
            if (!field1Box.getValue().equals(nameToRender)) {
                nameToRender = field1Box.getValue();
            }
        }
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int top = getY();
        int left = getX();
        int widgetWidth = Math.max(0, this.getWidth());

        int controlY = top + 4;
        int rightEdge = left + widgetWidth;

        field1Box.setY(controlY);
        field2Box.setY(controlY);
        removeButton.setY(controlY);

        int removeX = rightEdge - removeButton.getWidth() - REMOVE_BUTTON_MARGIN;
        removeButton.setX(removeX);

        int headX = left + HEAD_MARGIN_LEFT;
        int headY = controlY + Math.max(0, (FIELD_HEIGHT - HEAD_SIZE) / 2);

        int baseX = headX + HEAD_SIZE + FIELD_GAP;
        int maxFieldRight = removeX - PREVIEW_PADDING;
        int availableForFields = Math.max(0, maxFieldRight - baseX);

        int fieldWidth = availableForFields > 0 ? (availableForFields - FIELD_GAP) / 2 : FIELD_MIN_WIDTH;
        fieldWidth = Math.max(FIELD_MIN_WIDTH, Math.min(FIELD_MAX_WIDTH, fieldWidth));

        if (fieldWidth * 2 + FIELD_GAP > availableForFields) {
            fieldWidth = Math.max(FIELD_ABSOLUTE_MIN_WIDTH, (availableForFields - FIELD_GAP) / 2);
        }
        fieldWidth = Math.max(FIELD_ABSOLUTE_MIN_WIDTH, fieldWidth);

        int field1X = baseX;
        int field2X = field1X + fieldWidth + FIELD_GAP;

        if (field2X + fieldWidth > maxFieldRight) {
            field2X = Math.max(field1X, maxFieldRight - fieldWidth);
            field1X = Math.max(baseX, field2X - fieldWidth - FIELD_GAP);
        }

        field1Box.setX(field1X);
        field2Box.setX(field2X);
        field1Box.setWidth(fieldWidth);
        field2Box.setWidth(fieldWidth);

        int previewAreaLeft = field2X + fieldWidth + PREVIEW_PADDING;
        int previewAreaRight = removeX - PREVIEW_PADDING;
        if (previewAreaRight < previewAreaLeft) {
            previewAreaRight = previewAreaLeft;
        }

        Component previewComponent = this.preview != null ? this.preview : Component.empty();
        int previewAvailable = Math.max(0, previewAreaRight - previewAreaLeft);
        int previewY = controlY + (FIELD_HEIGHT - mc.font.lineHeight) / 2;
        if (previewAvailable > 0 && !previewComponent.getString().isEmpty()) {
            g.drawWordWrap(mc.font, previewComponent, previewAreaLeft, previewY, previewAvailable, 0xFFFFFFFF);
        }

        ClientRenderUtil.drawHead(g, nameToRender, headX, headY, HEAD_SIZE);

        field1Box.render(g, mouseX, mouseY, partialTick);
        field2Box.render(g, mouseX, mouseY, partialTick);
        removeButton.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
