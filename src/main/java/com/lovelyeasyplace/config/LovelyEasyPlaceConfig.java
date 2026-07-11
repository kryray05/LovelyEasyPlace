package com.lovelyeasyplace.config;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration manager for LovelyEasyPlace.
 * Stores settings in a simple properties file.
 */
public class LovelyEasyPlaceConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("lovelyeasyplace.properties");

    // Configuration values
    public static boolean enabled = true;
    public static boolean placeOnChests = true;
    public static boolean placeOnHoppers = true;
    public static boolean placeOnFurnaces = true;
    public static boolean placeOnDispensers = true;
    public static boolean placeOnDroppers = true;
    public static boolean placeOnTrappedChests = true;
    public static boolean placeOnBarrels = true;
    public static boolean placeOnShulkerBoxes = true;
    public static boolean placeOnSmokers = true;
    public static boolean placeOnBlastFurnaces = true;
    public static boolean showHudIndicator = true;
    public static boolean warnOnServerJoin = true;
    public static boolean holdMode = false;
    public static boolean debugLogging = false;
    public static int minPlacementIntervalMs = 0;
    public static List<String> disabledServers = new ArrayList<>();
    public static Set<String> warnedServers = new LinkedHashSet<>();

    /**
     * Load configuration from file.
     */
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save(); // Create default config
            return;
        }

        try (InputStream is = new FileInputStream(CONFIG_PATH.toFile())) {
            Properties props = new Properties();
            props.load(is);

            enabled = parseBoolean(props.getProperty("enabled", "true"));
            placeOnChests = parseBoolean(props.getProperty("placeOnChests", "true"));
            placeOnHoppers = parseBoolean(props.getProperty("placeOnHoppers", "true"));
            placeOnFurnaces = parseBoolean(props.getProperty("placeOnFurnaces", "true"));
            placeOnDispensers = parseBoolean(props.getProperty("placeOnDispensers", "true"));
            placeOnDroppers = parseBoolean(props.getProperty("placeOnDroppers", "true"));
            placeOnTrappedChests = parseBoolean(props.getProperty("placeOnTrappedChests", "true"));
            placeOnBarrels = parseBoolean(props.getProperty("placeOnBarrels", "true"));
            placeOnShulkerBoxes = parseBoolean(props.getProperty("placeOnShulkerBoxes", "true"));
            placeOnSmokers = parseBoolean(props.getProperty("placeOnSmokers", "true"));
            placeOnBlastFurnaces = parseBoolean(props.getProperty("placeOnBlastFurnaces", "true"));
            showHudIndicator = parseBoolean(props.getProperty("showHudIndicator", "true"));
            warnOnServerJoin = parseBoolean(props.getProperty("warnOnServerJoin", "true"));
            holdMode = parseBoolean(props.getProperty("holdMode", "false"));
            debugLogging = parseBoolean(props.getProperty("debugLogging", "false"));
            minPlacementIntervalMs = parseInt(props.getProperty("minPlacementIntervalMs", "0"), 0);
            disabledServers = parseList(props.getProperty("disabledServers", ""));
            warnedServers = new LinkedHashSet<>(parseList(props.getProperty("warnedServers", "")));
            normalize();

        } catch (IOException e) {
            LovelyEasyPlaceMod.LOGGER.error("Failed to load config", e);
        }
    }

    /**
     * Save configuration to file.
     */
    public static void save() {
        try {
            normalize();
            Files.createDirectories(CONFIG_PATH.getParent());

            Properties props = new Properties();
            props.setProperty("enabled", String.valueOf(enabled));
            props.setProperty("placeOnChests", String.valueOf(placeOnChests));
            props.setProperty("placeOnHoppers", String.valueOf(placeOnHoppers));
            props.setProperty("placeOnFurnaces", String.valueOf(placeOnFurnaces));
            props.setProperty("placeOnDispensers", String.valueOf(placeOnDispensers));
            props.setProperty("placeOnDroppers", String.valueOf(placeOnDroppers));
            props.setProperty("placeOnTrappedChests", String.valueOf(placeOnTrappedChests));
            props.setProperty("placeOnBarrels", String.valueOf(placeOnBarrels));
            props.setProperty("placeOnShulkerBoxes", String.valueOf(placeOnShulkerBoxes));
            props.setProperty("placeOnSmokers", String.valueOf(placeOnSmokers));
            props.setProperty("placeOnBlastFurnaces", String.valueOf(placeOnBlastFurnaces));
            props.setProperty("showHudIndicator", String.valueOf(showHudIndicator));
            props.setProperty("warnOnServerJoin", String.valueOf(warnOnServerJoin));
            props.setProperty("holdMode", String.valueOf(holdMode));
            props.setProperty("debugLogging", String.valueOf(debugLogging));
            props.setProperty("minPlacementIntervalMs", String.valueOf(minPlacementIntervalMs));
            props.setProperty("disabledServers", joinList(disabledServers));
            props.setProperty("warnedServers", joinList(warnedServers));

            try (OutputStream os = new FileOutputStream(CONFIG_PATH.toFile())) {
                props.store(os, "LovelyEasyPlace Configuration");
            }

        } catch (IOException e) {
            LovelyEasyPlaceMod.LOGGER.error("Failed to save config", e);
        }
    }

    /**
     * Create the config screen for Mod Menu integration.
     */
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.lovelyeasyplace.config.title"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startTextDescription(
                Text.translatable("text.lovelyeasyplace.config.warning"))
            .setColor(0xFFFFAA00)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.enabled"), enabled)
            .setDefaultValue(true)
            .setSaveConsumer(value -> enabled = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.show_hud_indicator"), showHudIndicator)
            .setDefaultValue(true)
            .setSaveConsumer(value -> showHudIndicator = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.hold_mode"), holdMode)
            .setDefaultValue(false)
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.hold_mode.tooltip"))
            .setSaveConsumer(value -> holdMode = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.warn_on_server_join"), warnOnServerJoin)
            .setDefaultValue(true)
            .setSaveConsumer(value -> warnOnServerJoin = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.debug_logging"), debugLogging)
            .setDefaultValue(false)
            .setSaveConsumer(value -> debugLogging = value)
            .build());

        ConfigCategory behavior = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.behavior"));

        behavior.addEntry(entryBuilder.startIntSlider(
                Text.translatable("text.lovelyeasyplace.config.min_placement_interval"), minPlacementIntervalMs, 0, 1000)
            .setDefaultValue(0)
            .setTextGetter(value -> value == 0
                ? Text.translatable("text.lovelyeasyplace.config.interval_disabled")
                : Text.translatable("text.lovelyeasyplace.config.interval_ms", value))
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.min_placement_interval.tooltip"))
            .setSaveConsumer(value -> minPlacementIntervalMs = value)
            .build());

        ConfigCategory server = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.server"));

        server.addEntry(entryBuilder.startStrList(
                Text.translatable("text.lovelyeasyplace.config.disabled_servers"), new ArrayList<>(disabledServers))
            .setDefaultValue(new ArrayList<>())
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.disabled_servers.tooltip"))
            .setSaveConsumer(value -> disabledServers = cleanList(value))
            .build());

        server.addEntry(entryBuilder.startStrList(
                Text.translatable("text.lovelyeasyplace.config.warned_servers"), new ArrayList<>(warnedServers))
            .setDefaultValue(new ArrayList<>())
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.warned_servers.tooltip"))
            .setSaveConsumer(value -> warnedServers = new LinkedHashSet<>(cleanList(value)))
            .build());

        ConfigCategory blocks = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.blocks"));

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_chests"), placeOnChests)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnChests = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_hoppers"), placeOnHoppers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnHoppers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_furnaces"), placeOnFurnaces)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnFurnaces = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_dispensers"), placeOnDispensers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnDispensers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_droppers"), placeOnDroppers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnDroppers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_trapped_chests"), placeOnTrappedChests)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnTrappedChests = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_barrels"), placeOnBarrels)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnBarrels = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_shulker_boxes"), placeOnShulkerBoxes)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnShulkerBoxes = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_smokers"), placeOnSmokers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnSmokers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_blast_furnaces"), placeOnBlastFurnaces)
            .setDefaultValue(true)
            .setSaveConsumer(value -> placeOnBlastFurnaces = value)
            .build());

        builder.setSavingRunnable(() -> {
            save();
            LovelyEasyPlaceMod.refreshRuntimeState();
        });

        builder.setAfterInitConsumer(screen -> Screens.getButtons(screen).add(ButtonWidget.builder(
                Text.translatable("text.lovelyeasyplace.config.reset_defaults"),
                button -> {
                    resetToDefaults();
                    save();
                    LovelyEasyPlaceMod.refreshRuntimeState();
                    MinecraftClient.getInstance().setScreen(createConfigScreen(parent));
                })
            .dimensions(Math.max(6, screen.width - 126), 6, 120, 20)
            .build()));

        return builder.build();
    }

    public static void resetToDefaults() {
        enabled = true;
        placeOnChests = true;
        placeOnHoppers = true;
        placeOnFurnaces = true;
        placeOnDispensers = true;
        placeOnDroppers = true;
        placeOnTrappedChests = true;
        placeOnBarrels = true;
        placeOnShulkerBoxes = true;
        placeOnSmokers = true;
        placeOnBlastFurnaces = true;
        showHudIndicator = true;
        warnOnServerJoin = true;
        holdMode = false;
        debugLogging = false;
        minPlacementIntervalMs = 0;
        disabledServers = new ArrayList<>();
        warnedServers = new LinkedHashSet<>();
    }

    public static boolean isServerDisabled(String serverAddress) {
        String normalizedServer = normalizeServer(serverAddress);
        if (normalizedServer.isEmpty()) {
            return false;
        }

        return disabledServers.stream()
            .map(LovelyEasyPlaceConfig::normalizeServer)
            .filter(value -> !value.isEmpty())
            .anyMatch(normalizedServer::contains);
    }

    public static boolean hasWarnedServer(String serverAddress) {
        String normalizedServer = normalizeServer(serverAddress);
        return warnedServers.stream()
            .map(LovelyEasyPlaceConfig::normalizeServer)
            .anyMatch(normalizedServer::equals);
    }

    private static boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static List<String> parseList(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        return cleanList(List.of(value.split(",")));
    }

    private static List<String> cleanList(Collection<String> values) {
        return values.stream()
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private static String joinList(Collection<String> values) {
        return cleanList(values).stream().collect(Collectors.joining(","));
    }

    private static String normalizeServer(String serverAddress) {
        if (serverAddress == null) {
            return "";
        }
        return serverAddress.trim().toLowerCase();
    }

    private static void normalize() {
        minPlacementIntervalMs = Math.max(0, Math.min(1000, minPlacementIntervalMs));
        disabledServers = cleanList(disabledServers);
        warnedServers = new LinkedHashSet<>(cleanList(warnedServers));
    }
}
