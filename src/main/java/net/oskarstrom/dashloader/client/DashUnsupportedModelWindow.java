package net.oskarstrom.dashloader.client;

import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.enums.DashDataType;

import javax.swing.text.StyledEditorKit;
import java.util.Map;

public class DashUnsupportedModelWindow extends Screen {
    private final DashRegistry registry;

    public DashUnsupportedModelWindow(Text title, DashRegistry registry) {
        super(title);
        this.registry = registry;
        registry.apiFailed.put(DashRegistry.class, DashDataType.MODEL);
        registry.apiFailed.put(UnicodeTextureFont.class, DashDataType.FONT);
        registry.apiFailed.put(StyledEditorKit.BoldAction.class, DashDataType.PROPERTY_VALUE);
        registry.apiFailed.put(Boolean.class, DashDataType.PROPERTY_VALUE);

    }

    @Override
    protected void init() {
        final int scaledHeight = client.getWindow().getScaledHeight();
        final int scaledWidth = client.getWindow().getScaledWidth();
        int x = scaledWidth / 8;
        int y2 = (scaledHeight / 8) * 6;
        final int y11 = 8 + y2;
        final int width = ((scaledWidth / 2) - x);
        final int spacer = 8;
        this.addDrawableChild(new ButtonWidget(x, y11, width - spacer, 20, Text.of("Disable Module"), (button) -> {
            System.out.println("disable");
        }));
        this.addDrawableChild(new ButtonWidget((x + width) + spacer, y11, width - spacer, 20, Text.of("Clear Cache"), (button) -> {
            System.out.println("clear");
        }));
        super.init();
    }

    private int inBetween(int one, int two) {
        return (int) (one + ((two - one) / 2f));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final int scaledHeight = client.getWindow().getScaledHeight();
        final int scaledWidth = client.getWindow().getScaledWidth();
        int x = scaledWidth / 8;
        int y = scaledHeight / 8;
        int x1 = (scaledWidth / 8) * 7;
        int y2 = (scaledHeight / 8) * 6;
        this.renderBackground(matrices);
        fill(matrices, x, y, x1, y2, 0x70000000);
        final int y1 = scaledHeight / 17;
        final int y3 = scaledHeight / 9;
        fill(matrices, x, y1, x1, y3, 0x70000000);
        drawCenteredText(matrices, textRenderer, "DashLoader found: " + registry.apiFailed.size() + " incompatible objects.", scaledWidth / 2, inBetween(y1, y3) - (textRenderer.fontHeight / 2), 0xffffff);
        int currentY = y + 16;
        for (Map.Entry<Class<?>, DashDataType> failedEntry : registry.apiFailed.entrySet()) {
            drawTextWithShadow(matrices, textRenderer, Text.of(failedEntry.getKey().getName()), x + 16, currentY, 0xffffff);
            final Text text = Text.of(failedEntry.getValue().name);
            drawTextWithShadow(matrices, textRenderer, text, (x1 - 16 - textRenderer.getWidth(text)), currentY, 0xffffff);
            currentY += textRenderer.fontHeight + 2;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}
