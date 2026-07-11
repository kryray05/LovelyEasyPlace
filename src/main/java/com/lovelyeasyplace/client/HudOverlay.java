package com.lovelyeasyplace.client;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

/**
 * Small HUD badge that shows whether LovelyEasyPlace can currently activate.
 */
public class HudOverlay implements HudRenderCallback {
    private static final int WIDTH = 54;
    private static final int HEIGHT = 14;
    private static final int PADDING = 6;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        if (!LovelyEasyPlaceConfig.showHudIndicator) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options == null || client.currentScreen != null) {
            return;
        }

        int x = context.getScaledWindowWidth() - WIDTH - PADDING;
        int y = PADDING;
        int accentColor = getAccentColor();
        Text label = getLabel();

        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xAA101010);
        context.fill(x, y, x + 3, y + HEIGHT, accentColor);
        context.drawTextWithShadow(client.textRenderer, label, x + 7, y + 3, 0xFFFFFFFF);
    }

    private static int getAccentColor() {
        if (LovelyEasyPlaceMod.isDisabledByServer()) {
            return 0xFFFFB000;
        }
        return LovelyEasyPlaceMod.isEnabled() ? 0xFF41D16F : 0xFFE05656;
    }

    private static Text getLabel() {
        if (LovelyEasyPlaceMod.isDisabledByServer()) {
            return Text.translatable("hud.lovelyeasyplace.blocked");
        }
        return Text.translatable(LovelyEasyPlaceMod.isEnabled()
            ? "hud.lovelyeasyplace.enabled"
            : "hud.lovelyeasyplace.disabled");
    }
}
