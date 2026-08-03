package com.lovelyeasyplace;

import com.lovelyeasyplace.client.HudOverlay;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfigScreenProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.lovelyeasyplace.integration.LitematicaAdapter;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LovelyEasyPlaceMod implements ClientModInitializer {

    public static final String MOD_ID = "lovelyeasyplace";
    public static final Logger LOGGER  = LoggerFactory.getLogger(MOD_ID);

    public static final Queue<Runnable> clickQueue = new ConcurrentLinkedQueue<>();

    private static KeyBinding toggleKey;
    private static KeyBinding holdKey;
    private static KeyBinding configKey;

    private static boolean placementSneaking = false;
    private static boolean sentSneakInput    = false;

    private static boolean holdActive         = false;
    private static boolean disabledByServer   = false;
    private static String  currentServerAddress = "singleplayer";
    private static String  pendingWarningServerAddress;
    private static long    lastPlacementSneakMs = 0L;
    private static int     placementSneakWatchdogTicks = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing LovelyEasyPlace");
        LovelyEasyPlaceConfig.load();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.toggle",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
            category
        ));
        holdKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.hold",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
            category
        ));
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lovelyeasyplace.config",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O,
            category
        ));
        HudRenderCallback.EVENT.register(new HudOverlay());
        registerPauseMenuButton();
        registerClientCommands();
        registerConnectionEvents();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isEnabled()) {
                clickQueue.clear();
            } else if (client.player != null && !clickQueue.isEmpty()) {
                Runnable task = clickQueue.poll();
                if (task != null) {
                    task.run();
                }
            }

            tickAutoPick(client);
            sendPendingServerWarning(client);
            handleToggleKey(client);
            handleHoldKey(client);
            handleConfigKey(client);

            // M1 Watchdog: Reset stuck sneak if interaction return was missed/errored
            if (placementSneaking) {
                placementSneakWatchdogTicks++;
                if (placementSneakWatchdogTicks > 3) {
                    resetPlacementSneak(client.player);
                }
            } else {
                placementSneakWatchdogTicks = 0;
            }
        });

        LOGGER.info("LovelyEasyPlace initialized");
    }

    private static void tickAutoPick(MinecraftClient client) {
        if (!isEnabled() || !LovelyEasyPlaceConfig.autoPickFromInventory || client.player == null || client.interactionManager == null) {
            return;
        }
        if (client.crosshairTarget instanceof BlockHitResult blockHit) {
            BlockPos clickedPos = blockHit.getBlockPos();
            World world = client.player.getEntityWorld();
            BlockState clickedState = world.getBlockState(clickedPos);
            BlockPos placedPos = clickedState.isReplaceable() ? clickedPos : clickedPos.offset(blockHit.getSide());

            BlockState schematic = LitematicaAdapter.getSchematicState(world, placedPos);
            if (schematic == null || schematic.isAir()) {
                schematic = LitematicaAdapter.getSchematicState(world, clickedPos);
            }

            if (schematic != null && !schematic.isAir()) {
                Item targetItem = schematic.getBlock().asItem();
                if (targetItem != null && targetItem != Items.AIR) {
                    if (!client.player.getStackInHand(Hand.MAIN_HAND).isOf(targetItem)
                            && !client.player.getStackInHand(Hand.OFF_HAND).isOf(targetItem)) {
                        for (int i = 0; i < 36; i++) {
                            ItemStack stack = client.player.getInventory().getStack(i);
                            if (stack.isOf(targetItem)) {
                                pickOrSwapSlot(client.player, client.interactionManager, i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void pickOrSwapSlot(ClientPlayerEntity player, net.minecraft.client.network.ClientPlayerInteractionManager interactionManager, int slot) {
        if (PlayerInventory.isValidHotbarIndex(slot)) {
            player.getInventory().setSelectedSlot(slot);
        } else if (slot >= 9 && slot < 36) {
            int currentSelected = player.getInventory().getSelectedSlot();
            player.getInventory().swapSlotWithHotbar(slot);
            if (interactionManager != null && player.playerScreenHandler != null) {
                interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,
                    slot,
                    currentSelected,
                    SlotActionType.SWAP,
                    player
                );
            }
        }
    }

    private static void registerPauseMenuButton() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof GameMenuScreen)) return;
            Screens.getButtons(screen).add(ButtonWidget.builder(
                Text.translatable("text.lovelyeasyplace.open_config"),
                btn -> client.setScreen(LovelyEasyPlaceConfigScreenProvider.createScreen(screen))
            ).dimensions(Math.max(6, w - 106), 6, 100, 20).build());
        });
    }

    private static void handleConfigKey(MinecraftClient client) {
        while (configKey.wasPressed()) {
            LOGGER.info("Config key pressed! Opening config screen.");
            try {
                client.setScreen(LovelyEasyPlaceConfigScreenProvider.createScreen(client.currentScreen));
            } catch (Exception e) {
                LOGGER.error("Failed to open config screen", e);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Failed to open config screen: " + e.toString()).formatted(Formatting.RED), false);
                }
            }
        }
    }

    private static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("lep")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.translatable("message.lovelyeasyplace.command.help"));
                    return 1;
                })
                .then(literal("toggle").executes(ctx -> {
                    setEnabled(!LovelyEasyPlaceConfig.enabled);
                    sendStateMessage(ctx.getSource().getClient());
                    return 1;
                }))
                .then(literal("config").executes(ctx -> {
                    MinecraftClient client = ctx.getSource().getClient();
                    client.send(() -> client.setScreen(LovelyEasyPlaceConfigScreenProvider.createScreen(client.currentScreen)));
                    return 1;
                }))
                .then(literal("reset").executes(ctx -> {
                    LovelyEasyPlaceConfig.resetToDefaults();
                    LovelyEasyPlaceConfig.save();
                    refreshRuntimeState();
                    ctx.getSource().sendFeedback(Text.translatable("message.lovelyeasyplace.reset_defaults"));
                    return 1;
                }))
            )
        );
    }

    private static void registerConnectionEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            currentServerAddress = getServerAddress(client);
            disabledByServer = isMultiplayer(client)
                && (!LovelyEasyPlaceConfig.allowMultiplayer
                    || LovelyEasyPlaceConfig.isServerDisabled(currentServerAddress));

            if (disabledByServer) {
                resetPlacementSneak(client.player);
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.translatable("message.lovelyeasyplace.disabled_server", currentServerAddress), true);
                }
                return;
            }

            if (shouldWarnForServer(client)) pendingWarningServerAddress = currentServerAddress;
            refreshRuntimeState();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetPlacementSneak(client.player);
            clickQueue.clear();
            disabledByServer = false;
            currentServerAddress = "singleplayer";
            pendingWarningServerAddress = null;
            holdActive = false;
        });
    }

    private static void handleToggleKey(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            if (LovelyEasyPlaceConfig.holdMode) continue;
            setEnabled(!LovelyEasyPlaceConfig.enabled);
            sendStateMessage(client);
        }
    }

    private static void handleHoldKey(MinecraftClient client) {
        if (!LovelyEasyPlaceConfig.holdMode) {
            if (holdActive) { holdActive = false; refreshRuntimeState(); }
            return;
        }
        boolean pressed = holdKey.isPressed();
        if (pressed == holdActive) return;
        holdActive = pressed;
        if (!isEnabled()) resetPlacementSneak(client.player);
        sendStateMessage(client);
    }

    public static void beginPlacementSneak(ClientPlayerEntity player) {
        if (placementSneaking || !isEnabled() || player == null || player.input == null) return;

        PlayerInput current = player.input.playerInput;
        if (current.sneak()) return; // already sneaking

        long now = System.currentTimeMillis();
        if (LovelyEasyPlaceConfig.minPlacementIntervalMs > 0
                && now - lastPlacementSneakMs < LovelyEasyPlaceConfig.minPlacementIntervalMs) {
            return;
        }

        placementSneaking    = true;
        lastPlacementSneakMs = now;
        sentSneakInput       = true;
        PlayerInput modified = new PlayerInput(
            current.forward(),
            current.backward(),
            current.left(),
            current.right(),
            current.jump(),
            true, // sneak
            current.sprint()
        );
        player.input.playerInput = modified;
        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(new PlayerInputC2SPacket(modified));
        }
        debugLog("Started fake sneak at " + Instant.now());
    }

    public static void endPlacementSneak(ClientPlayerEntity player) {
        resetPlacementSneak(player);
    }

    public static void resetPlacementSneak(ClientPlayerEntity player) {
        if (!placementSneaking) return;
        if (sentSneakInput && player != null && player.input != null) {
            PlayerInput current = player.input.playerInput;
            PlayerInput restored = new PlayerInput(
                current.forward(),
                current.backward(),
                current.left(),
                current.right(),
                current.jump(),
                false, // sneak
                current.sprint()
            );
            player.input.playerInput = restored;
            debugLog("Restored sneak input at " + Instant.now());
        }
        placementSneaking = false;
        sentSneakInput    = false;
    }

    public static boolean shouldFakeSneak() {
        return isEnabled() && placementSneaking;
    }

    public static boolean isEnabled() {
        return !disabledByServer && (LovelyEasyPlaceConfig.holdMode ? holdActive : LovelyEasyPlaceConfig.enabled);
    }

    public static void setEnabled(boolean enabled) {
        LovelyEasyPlaceConfig.enabled = enabled;
        LovelyEasyPlaceConfig.save();
        refreshRuntimeState();
    }

    public static void refreshRuntimeState() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (isMultiplayer(client)) {
            currentServerAddress = getServerAddress(client);
            disabledByServer = !LovelyEasyPlaceConfig.allowMultiplayer
                || LovelyEasyPlaceConfig.isServerDisabled(currentServerAddress);
        } else {
            disabledByServer = false;
            currentServerAddress = "singleplayer";
        }
        if (!LovelyEasyPlaceConfig.holdMode) holdActive = false;
        if (!isEnabled()) {
            resetPlacementSneak(client.player);
            clickQueue.clear();
        }
    }

    public static boolean isDisabledByServer()    { return disabledByServer; }
    public static String  getCurrentServerAddress() { return currentServerAddress; }

    private static String getServerAddress(MinecraftClient client) {
        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address != null) {
            return client.getCurrentServerEntry().address;
        }
        return "multiplayer";
    }

    private static boolean isMultiplayer(MinecraftClient client) {
        return !client.isInSingleplayer();
    }

    private static boolean shouldWarnForServer(MinecraftClient client) {
        return isMultiplayer(client)
            && LovelyEasyPlaceConfig.warnOnServerJoin
            && LovelyEasyPlaceConfig.allowMultiplayer
            && (LovelyEasyPlaceConfig.enabled || LovelyEasyPlaceConfig.holdMode)
            && !LovelyEasyPlaceConfig.hasWarnedServer(currentServerAddress);
    }

    private static void sendPendingServerWarning(MinecraftClient client) {
        if (pendingWarningServerAddress == null || client.player == null) return;
        if (!pendingWarningServerAddress.equals(currentServerAddress) || !shouldWarnForServer(client)) {
            pendingWarningServerAddress = null;
            return;
        }

        client.player.sendMessage(
            Text.literal("[LovelyEasyPlace] ").formatted(Formatting.YELLOW)
                .append(Text.translatable("message.lovelyeasyplace.server_warning").formatted(Formatting.WHITE))
                .append(" ")
                .append(Text.translatable("message.lovelyeasyplace.press_to_configure",
                    configKey.getBoundKeyLocalizedText()).formatted(Formatting.AQUA)),
            false
        );
        LovelyEasyPlaceConfig.warnedServers.add(pendingWarningServerAddress);
        LovelyEasyPlaceConfig.save();
        pendingWarningServerAddress = null;
    }

    private static void sendStateMessage(MinecraftClient client) {
        if (client.player == null) return;
        Text msg = disabledByServer
            ? Text.translatable("message.lovelyeasyplace.disabled_server", currentServerAddress)
            : Text.translatable(isEnabled() ? "message.lovelyeasyplace.enabled" : "message.lovelyeasyplace.disabled");
        client.player.sendMessage(msg, true);
    }

    public static void debugLog(String message) {
        if (LovelyEasyPlaceConfig.debugLogging) LOGGER.info("[debug] {}", message);
    }

}
