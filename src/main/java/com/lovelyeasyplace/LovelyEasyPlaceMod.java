package com.lovelyeasyplace;

import com.lovelyeasyplace.client.HudOverlay;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * LovelyEasyPlace - Quality of Life mod for easier block placement.
 *
 * Temporarily sends normal vanilla input packets while placing against
 * supported interactive blocks, then restores the previous sneak state.
 */
public class LovelyEasyPlaceMod implements ClientModInitializer {

    public static final String MOD_ID = "lovelyeasyplace";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final KeyBinding.Category KEY_CATEGORY =
        KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    // Key binding to toggle the mod on/off
    private static KeyBinding toggleKey;
    private static KeyBinding holdKey;
    private static KeyBinding configKey;

    // Track if a placement-scoped sneak is currently active.
    private static boolean placementSneaking = false;
    private static boolean sentSneakInput = false;
    private static PlayerInput previousInput = PlayerInput.DEFAULT;

    private static boolean holdActive = false;
    private static boolean disabledByServer = false;
    private static String currentServerAddress = "singleplayer";
    private static long lastPlacementSneakMs = 0L;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing LovelyEasyPlace");

        // Load configuration
        LovelyEasyPlaceConfig.load();

        // Register toggle key binding
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN, // Unbound by default
            KEY_CATEGORY
        ));

        holdKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.hold",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            KEY_CATEGORY
        ));

        HudRenderCallback.EVENT.register(new HudOverlay());
        registerPauseMenuButton();
        registerClientCommands();
        registerConnectionEvents();

        // Register tick event to handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleToggleKey(client);
            handleHoldKey(client);
            handleConfigKey(client);
        });

        LOGGER.info("LovelyEasyPlace initialized");
    }

    private static void registerPauseMenuButton() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof GameMenuScreen)) {
                return;
            }

            Screens.getButtons(screen).add(ButtonWidget.builder(
                    Text.translatable("text.lovelyeasyplace.open_config"),
                    button -> client.setScreen(LovelyEasyPlaceConfig.createConfigScreen(screen)))
                .dimensions(Math.max(6, scaledWidth - 106), 6, 100, 20)
                .build());
        });
    }

    private static void handleConfigKey(MinecraftClient client) {
        while (configKey.wasPressed()) {
            client.setScreen(LovelyEasyPlaceConfig.createConfigScreen(client.currentScreen));
        }
    }

    private static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("lep")
                .executes(context -> {
                    context.getSource().sendFeedback(Text.translatable("message.lovelyeasyplace.command.help"));
                    return 1;
                })
                .then(literal("toggle").executes(context -> {
                    setEnabled(!LovelyEasyPlaceConfig.enabled);
                    sendStateMessage(context.getSource().getClient());
                    return 1;
                }))
                .then(literal("reset").executes(context -> {
                    LovelyEasyPlaceConfig.resetToDefaults();
                    LovelyEasyPlaceConfig.save();
                    refreshRuntimeState();
                    context.getSource().sendFeedback(Text.translatable("message.lovelyeasyplace.reset_defaults"));
                    return 1;
                }))
            )
        );
    }

    private static void registerConnectionEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            currentServerAddress = getServerAddress(client);
            disabledByServer = isMultiplayerServer(client)
                && LovelyEasyPlaceConfig.isServerDisabled(currentServerAddress);

            if (disabledByServer) {
                resetPlacementSneak(client.player);
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.translatable("message.lovelyeasyplace.disabled_server", currentServerAddress),
                        true
                    );
                }
                debugLog("Disabled on configured server " + currentServerAddress);
                return;
            }

            if (shouldWarnForServer(client)) {
                sendServerWarning(client);
                LovelyEasyPlaceConfig.warnedServers.add(currentServerAddress);
                LovelyEasyPlaceConfig.save();
            }

            refreshRuntimeState();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetPlacementSneak(client.player);
            disabledByServer = false;
            currentServerAddress = "singleplayer";
            holdActive = false;
        });
    }

    private static void handleToggleKey(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            if (LovelyEasyPlaceConfig.holdMode) {
                continue;
            }

            setEnabled(!LovelyEasyPlaceConfig.enabled);
            sendStateMessage(client);
        }
    }

    private static void handleHoldKey(MinecraftClient client) {
        if (!LovelyEasyPlaceConfig.holdMode) {
            if (holdActive) {
                holdActive = false;
                refreshRuntimeState();
            }
            return;
        }

        boolean pressed = holdKey.isPressed();
        if (pressed == holdActive) {
            return;
        }

        holdActive = pressed;
        if (!isEnabled()) {
            resetPlacementSneak(client.player);
        }
        sendStateMessage(client);
    }

    /**
     * Begin a single block placement with vanilla sneak state enabled.
     */
    public static void beginPlacementSneak(ClientPlayerEntity player) {
        if (placementSneaking || !isEnabled()) {
            return;
        }

        PlayerInput currentInput = getCurrentInput(player);
        if (currentInput.sneak()) {
            debugLog("Skipped fake sneak because the player is already sneaking");
            return;
        }

        long now = System.currentTimeMillis();
        if (LovelyEasyPlaceConfig.minPlacementIntervalMs > 0
            && now - lastPlacementSneakMs < LovelyEasyPlaceConfig.minPlacementIntervalMs) {
            debugLog("Skipped fake sneak during placement cooldown");
            return;
        }

        placementSneaking = true;
        lastPlacementSneakMs = now;
        previousInput = currentInput;
        sentSneakInput = true;
        sendInput(player, withSneak(currentInput, true));
        debugLog("Started fake sneak at " + Instant.now());
    }

    /**
     * Restore the player's previous sneak state after a placement attempt.
     */
    public static void endPlacementSneak(ClientPlayerEntity player) {
        resetPlacementSneak(player);
    }

    /**
     * Force-clear any active placement-scoped sneak state.
     */
    public static void resetPlacementSneak(ClientPlayerEntity player) {
        if (!placementSneaking) {
            return;
        }

        if (sentSneakInput && player != null) {
            sendInput(player, previousInput);
            debugLog("Restored previous sneak input at " + Instant.now());
        }

        placementSneaking = false;
        sentSneakInput = false;
        previousInput = PlayerInput.DEFAULT;
    }

    private static PlayerInput getCurrentInput(ClientPlayerEntity player) {
        if (player.input != null && player.input.playerInput != null) {
            return player.input.playerInput;
        }
        return PlayerInput.DEFAULT;
    }

    private static PlayerInput withSneak(PlayerInput input, boolean sneak) {
        return new PlayerInput(
            input.forward(),
            input.backward(),
            input.left(),
            input.right(),
            input.jump(),
            sneak,
            input.sprint()
        );
    }

    private static void sendInput(ClientPlayerEntity player, PlayerInput input) {
        if (player.input != null) {
            player.input.playerInput = input;
        }

        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(new PlayerInputC2SPacket(input));
        }
    }

    /**
     * Check if client-side placement prediction should see the player sneaking.
     */
    public static boolean shouldFakeSneak() {
        return isEnabled() && placementSneaking;
    }

    /**
     * Check if the mod is enabled.
     */
    public static boolean isEnabled() {
        return !disabledByServer && (LovelyEasyPlaceConfig.holdMode ? holdActive : LovelyEasyPlaceConfig.enabled);
    }

    /**
     * Set the saved enabled state.
     */
    public static void setEnabled(boolean enabled) {
        LovelyEasyPlaceConfig.enabled = enabled;
        LovelyEasyPlaceConfig.save();
        refreshRuntimeState();
    }

    public static void refreshRuntimeState() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (isMultiplayerServer(client)) {
            currentServerAddress = getServerAddress(client);
            disabledByServer = LovelyEasyPlaceConfig.isServerDisabled(currentServerAddress);
        } else {
            disabledByServer = false;
            currentServerAddress = "singleplayer";
        }

        if (!LovelyEasyPlaceConfig.holdMode) {
            holdActive = false;
        }

        if (!isEnabled()) {
            resetPlacementSneak(client.player);
        }
    }

    public static boolean isDisabledByServer() {
        return disabledByServer;
    }

    public static String getCurrentServerAddress() {
        return currentServerAddress;
    }

    private static String getServerAddress(MinecraftClient client) {
        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address != null) {
            return client.getCurrentServerEntry().address;
        }
        return "singleplayer";
    }

    private static boolean isMultiplayerServer(MinecraftClient client) {
        return client.getCurrentServerEntry() != null && !client.isInSingleplayer();
    }

    private static boolean shouldWarnForServer(MinecraftClient client) {
        return isMultiplayerServer(client)
            && LovelyEasyPlaceConfig.warnOnServerJoin
            && (LovelyEasyPlaceConfig.enabled || LovelyEasyPlaceConfig.holdMode)
            && !LovelyEasyPlaceConfig.hasWarnedServer(currentServerAddress);
    }

    private static void sendServerWarning(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        Text warning = Text.literal("[LovelyEasyPlace] ")
            .formatted(Formatting.YELLOW)
            .append(Text.translatable("message.lovelyeasyplace.server_warning").formatted(Formatting.WHITE))
            .append(" ")
            .append(Text.translatable(
                "message.lovelyeasyplace.press_to_configure",
                configKey.getBoundKeyLocalizedText()
            ).formatted(Formatting.AQUA));

        client.player.sendMessage(warning, false);
    }

    private static void sendStateMessage(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        Text message;
        if (disabledByServer) {
            message = Text.translatable("message.lovelyeasyplace.disabled_server", currentServerAddress);
        } else {
            message = Text.translatable(isEnabled()
                ? "message.lovelyeasyplace.enabled"
                : "message.lovelyeasyplace.disabled");
        }
        client.player.sendMessage(message, true);
    }

    private static void debugLog(String message) {
        if (LovelyEasyPlaceConfig.debugLogging) {
            LOGGER.info("[debug] {}", message);
        }
    }
}
