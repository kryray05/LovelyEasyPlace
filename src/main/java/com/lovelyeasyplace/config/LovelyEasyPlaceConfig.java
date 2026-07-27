package com.lovelyeasyplace.config;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class LovelyEasyPlaceConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("lovelyeasyplace.properties");
    private static final List<String> BUILT_IN_DISABLED_SERVERS = List.of("hypixel.net", "mccisland.net", "cubecraft.net", "wynncraft.com", "manacube.com");

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
    public static boolean placeOnCrafters = true;
    public static boolean placeOnAnvils = true;
    public static boolean placeOnCraftingTables = true;
    public static boolean placeOnEnderChests = true;
    public static boolean placeOnLooms = true;
    public static boolean placeOnCartographyTables = true;
    public static boolean placeOnGrindstones = true;
    public static boolean placeOnStonecutters = true;
    public static boolean placeOnSmithingTables = true;
    public static boolean placeOnBrewingStands = true;
    public static boolean placeOnBeacons = true;
    public static boolean placeOnEnchantingTables = true;
    public static boolean placeOnLecterns = true;
    public static boolean placeOnChiseledBookshelves = true;
    public static boolean placeOnJukeboxes = true;
    public static boolean placeOnNoteBlocks = true;
    public static boolean showHudIndicator = true;
    public static final boolean warnOnServerJoin = true;
    public static boolean holdMode = false;
    public static boolean debugLogging = false;
    public static int minPlacementIntervalMs = 0;
    
    // Custom requested features
    public static boolean autoRotate = true;
    public static boolean reversePlacement = false;
    public static boolean autoNoteBlockPitch = true;

    public static List<String> disabledServers = new ArrayList<>();
    public static Set<String> warnedServers = new LinkedHashSet<>();

    public static void load() {
        if (!Files.exists(CONFIG_PATH, new LinkOption[0])) {
            LovelyEasyPlaceConfig.save();
            return;
        }
        try (FileInputStream is = new FileInputStream(CONFIG_PATH.toFile())) {
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
            placeOnCrafters = parseBoolean(props.getProperty("placeOnCrafters", "true"));
            placeOnAnvils = parseBoolean(props.getProperty("placeOnAnvils", "true"));
            placeOnCraftingTables = parseBoolean(props.getProperty("placeOnCraftingTables", "true"));
            placeOnEnderChests = parseBoolean(props.getProperty("placeOnEnderChests", "true"));
            placeOnLooms = parseBoolean(props.getProperty("placeOnLooms", "true"));
            placeOnCartographyTables = parseBoolean(props.getProperty("placeOnCartographyTables", "true"));
            placeOnGrindstones = parseBoolean(props.getProperty("placeOnGrindstones", "true"));
            placeOnStonecutters = parseBoolean(props.getProperty("placeOnStonecutters", "true"));
            placeOnSmithingTables = parseBoolean(props.getProperty("placeOnSmithingTables", "true"));
            placeOnBrewingStands = parseBoolean(props.getProperty("placeOnBrewingStands", "true"));
            placeOnBeacons = parseBoolean(props.getProperty("placeOnBeacons", "true"));
            placeOnEnchantingTables = parseBoolean(props.getProperty("placeOnEnchantingTables", "true"));
            placeOnLecterns = parseBoolean(props.getProperty("placeOnLecterns", "true"));
            placeOnChiseledBookshelves = parseBoolean(props.getProperty("placeOnChiseledBookshelves", "true"));
            placeOnJukeboxes = parseBoolean(props.getProperty("placeOnJukeboxes", "true"));
            placeOnNoteBlocks = parseBoolean(props.getProperty("placeOnNoteBlocks", "true"));
            showHudIndicator = parseBoolean(props.getProperty("showHudIndicator", "true"));
            holdMode = parseBoolean(props.getProperty("holdMode", "false"));
            debugLogging = parseBoolean(props.getProperty("debugLogging", "false"));
            minPlacementIntervalMs = parseInt(props.getProperty("minPlacementIntervalMs", "0"), 0);
            
            autoRotate = parseBoolean(props.getProperty("autoRotate", "true"));
            reversePlacement = parseBoolean(props.getProperty("reversePlacement", "false"));
            autoNoteBlockPitch = parseBoolean(props.getProperty("autoNoteBlockPitch", "true"));

            disabledServers = parseList(props.getProperty("disabledServers", ""));
            warnedServers = new LinkedHashSet<>(parseList(props.getProperty("warnedServers", "")));
            normalize();
        } catch (IOException e) {
            LovelyEasyPlaceMod.LOGGER.error("Failed to load config", e);
        }
    }

    public static void save() {
        try {
            normalize();
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
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
            props.setProperty("placeOnCrafters", String.valueOf(placeOnCrafters));
            props.setProperty("placeOnAnvils", String.valueOf(placeOnAnvils));
            props.setProperty("placeOnCraftingTables", String.valueOf(placeOnCraftingTables));
            props.setProperty("placeOnEnderChests", String.valueOf(placeOnEnderChests));
            props.setProperty("placeOnLooms", String.valueOf(placeOnLooms));
            props.setProperty("placeOnCartographyTables", String.valueOf(placeOnCartographyTables));
            props.setProperty("placeOnGrindstones", String.valueOf(placeOnGrindstones));
            props.setProperty("placeOnStonecutters", String.valueOf(placeOnStonecutters));
            props.setProperty("placeOnSmithingTables", String.valueOf(placeOnSmithingTables));
            props.setProperty("placeOnBrewingStands", String.valueOf(placeOnBrewingStands));
            props.setProperty("placeOnBeacons", String.valueOf(placeOnBeacons));
            props.setProperty("placeOnEnchantingTables", String.valueOf(placeOnEnchantingTables));
            props.setProperty("placeOnLecterns", String.valueOf(placeOnLecterns));
            props.setProperty("placeOnChiseledBookshelves", String.valueOf(placeOnChiseledBookshelves));
            props.setProperty("placeOnJukeboxes", String.valueOf(placeOnJukeboxes));
            props.setProperty("placeOnNoteBlocks", String.valueOf(placeOnNoteBlocks));
            props.setProperty("showHudIndicator", String.valueOf(showHudIndicator));
            props.setProperty("warnOnServerJoin", "true");
            props.setProperty("holdMode", String.valueOf(holdMode));
            props.setProperty("debugLogging", String.valueOf(debugLogging));
            props.setProperty("minPlacementIntervalMs", String.valueOf(minPlacementIntervalMs));
            
            props.setProperty("autoRotate", String.valueOf(autoRotate));
            props.setProperty("reversePlacement", String.valueOf(reversePlacement));
            props.setProperty("autoNoteBlockPitch", String.valueOf(autoNoteBlockPitch));

            props.setProperty("disabledServers", joinList(disabledServers));
            props.setProperty("warnedServers", joinList(warnedServers));
            try (FileOutputStream os = new FileOutputStream(CONFIG_PATH.toFile())) {
                props.store(os, "LovelyEasyPlace Configuration");
            }
        } catch (IOException e) {
            LovelyEasyPlaceMod.LOGGER.error("Failed to save config", e);
        }
    }

    public static Screen createConfigScreen(Screen parent) {
        return LovelyEasyPlaceConfigScreenProvider.createScreen(parent);
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
        placeOnCrafters = true;
        placeOnAnvils = true;
        placeOnCraftingTables = true;
        placeOnEnderChests = true;
        placeOnLooms = true;
        placeOnCartographyTables = true;
        placeOnGrindstones = true;
        placeOnStonecutters = true;
        placeOnSmithingTables = true;
        placeOnBrewingStands = true;
        placeOnBeacons = true;
        placeOnEnchantingTables = true;
        placeOnLecterns = true;
        placeOnChiseledBookshelves = true;
        placeOnJukeboxes = true;
        placeOnNoteBlocks = true;
        showHudIndicator = true;
        holdMode = false;
        debugLogging = false;
        minPlacementIntervalMs = 0;
        
        autoRotate = true;
        reversePlacement = false;
        autoNoteBlockPitch = true;

        disabledServers = new ArrayList<>();
        warnedServers = new LinkedHashSet<>();
    }

    public static boolean isServerDisabled(String serverAddress) {
        String normalizedServer = normalizeServer(serverAddress);
        if (normalizedServer.isEmpty()) {
            return false;
        }
        if (BUILT_IN_DISABLED_SERVERS.stream().anyMatch(normalizedServer::contains)) {
            return true;
        }
        return disabledServers.stream()
                .map(LovelyEasyPlaceConfig::normalizeServer)
                .filter(value -> !value.isEmpty())
                .anyMatch(normalizedServer::contains);
    }

    public static boolean hasWarnedServer(String serverAddress) {
        String normalizedServer = normalizeServer(serverAddress);
        return warnedServers.stream().map(LovelyEasyPlaceConfig::normalizeServer).anyMatch(normalizedServer::equals);
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
        return values.stream().map(String::trim).filter(value -> !value.isEmpty()).distinct().collect(Collectors.toCollection(ArrayList::new));
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
