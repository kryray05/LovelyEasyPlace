package com.lovelyeasyplace.config;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LovelyEasyPlaceFallbackConfigScreen extends Screen {
    private final Screen parent;

    public LovelyEasyPlaceFallbackConfigScreen(Screen parent) {
        super(Text.literal("LovelyEasyPlace Lite Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int btnWidth = 150;
        int btnHeight = 20;
        int spacing = 24;
        
        int startX1 = this.width / 2 - 155;
        int startX2 = this.width / 2 + 5;
        int startY = 60;

        // Column 1 Buttons
        // 1. Enable Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getEnabledText(),
            btn -> {
                LovelyEasyPlaceConfig.enabled = !LovelyEasyPlaceConfig.enabled;
                btn.setMessage(getEnabledText());
            }
        ).dimensions(startX1, startY, btnWidth, btnHeight).build());

        // 2. Hold Mode Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getHoldModeText(),
            btn -> {
                LovelyEasyPlaceConfig.holdMode = !LovelyEasyPlaceConfig.holdMode;
                btn.setMessage(getHoldModeText());
            }
        ).dimensions(startX1, startY + spacing, btnWidth, btnHeight).build());

        // 3. Show HUD Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getShowHudText(),
            btn -> {
                LovelyEasyPlaceConfig.showHudIndicator = !LovelyEasyPlaceConfig.showHudIndicator;
                btn.setMessage(getShowHudText());
            }
        ).dimensions(startX1, startY + spacing * 2, btnWidth, btnHeight).build());

        // Column 2 Buttons
        // 1. Litematica Auto Rotate Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getAutoRotateText(),
            btn -> {
                LovelyEasyPlaceConfig.autoRotate = !LovelyEasyPlaceConfig.autoRotate;
                btn.setMessage(getAutoRotateText());
            }
        ).dimensions(startX2, startY, btnWidth, btnHeight).build());

        // 2. Reverse Placement Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getReversePlacementText(),
            btn -> {
                LovelyEasyPlaceConfig.reversePlacement = !LovelyEasyPlaceConfig.reversePlacement;
                btn.setMessage(getReversePlacementText());
            }
        ).dimensions(startX2, startY + spacing, btnWidth, btnHeight).build());

        // 3. Note Block Pitch Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getNoteBlockPitchText(),
            btn -> {
                LovelyEasyPlaceConfig.autoNoteBlockPitch = !LovelyEasyPlaceConfig.autoNoteBlockPitch;
                btn.setMessage(getNoteBlockPitchText());
            }
        ).dimensions(startX2, startY + spacing * 2, btnWidth, btnHeight).build());

        // 4. Match Redstone States Toggle
        this.addDrawableChild(ButtonWidget.builder(
            getMatchRedstoneStatesText(),
            btn -> {
                LovelyEasyPlaceConfig.matchRedstoneStates = !LovelyEasyPlaceConfig.matchRedstoneStates;
                btn.setMessage(getMatchRedstoneStatesText());
            }
        ).dimensions(startX2, startY + spacing * 3, btnWidth, btnHeight).build());

        // Bottom Control Buttons
        int bottomY = startY + spacing * 4 + 10;
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            btn -> {
                LovelyEasyPlaceConfig.save();
                LovelyEasyPlaceMod.refreshRuntimeState();
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }
        ).dimensions(this.width / 2 - 100, bottomY, 200, 20).build());
    }

    private Text getEnabledText() {
        return Text.literal("LEP Assisted Placement: " + (LovelyEasyPlaceConfig.enabled ? "ON" : "OFF"));
    }

    private Text getHoldModeText() {
        return Text.literal("Hold-to-Activate Mode: " + (LovelyEasyPlaceConfig.holdMode ? "ON" : "OFF"));
    }

    private Text getShowHudText() {
        return Text.literal("Show HUD Indicator: " + (LovelyEasyPlaceConfig.showHudIndicator ? "ON" : "OFF"));
    }

    private Text getAutoRotateText() {
        return Text.literal("Litematica Auto-Rotate: " + (LovelyEasyPlaceConfig.autoRotate ? "ON" : "OFF"));
    }

    private Text getReversePlacementText() {
        return Text.literal("Reverse Placement: " + (LovelyEasyPlaceConfig.reversePlacement ? "ON" : "OFF"));
    }

    private Text getNoteBlockPitchText() {
        return Text.literal("Match Note Block Pitch: " + (LovelyEasyPlaceConfig.autoNoteBlockPitch ? "ON" : "OFF"));
    }

    private Text getMatchRedstoneStatesText() {
        return Text.literal("Match Redstone States: " + (LovelyEasyPlaceConfig.matchRedstoneStates ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("LovelyEasyPlace Settings"), this.width / 2, 15, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Using Lite Settings Screen (Cloth Config disabled or incompatible)").formatted(Formatting.YELLOW), this.width / 2, 30, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Adjust other options in config/lovelyeasyplace.properties").formatted(Formatting.GRAY), this.width / 2, 42, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        LovelyEasyPlaceConfig.save();
        LovelyEasyPlaceMod.refreshRuntimeState();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
