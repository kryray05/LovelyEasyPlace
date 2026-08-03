package com.lovelyeasyplace.config;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigScreenCreator {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.lovelyeasyplace.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.general"));

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.enabled"), LovelyEasyPlaceConfig.enabled)
            .setDefaultValue(true)
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.enabled.tooltip"))
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.enabled = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.hold_mode"), LovelyEasyPlaceConfig.holdMode)
            .setDefaultValue(false)
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.hold_mode.tooltip"))
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.holdMode = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.show_hud"), LovelyEasyPlaceConfig.showHudIndicator)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.showHudIndicator = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.debug_logging"), LovelyEasyPlaceConfig.debugLogging)
            .setDefaultValue(false)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.debugLogging = value)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.allow_multiplayer"), LovelyEasyPlaceConfig.allowMultiplayer)
            .setDefaultValue(false)
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.allow_multiplayer.tooltip"))
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.allowMultiplayer = value)
            .build());

        ConfigCategory blocks = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.blocks"));

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_chests"), LovelyEasyPlaceConfig.placeOnChests)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnChests = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_trapped_chests"), LovelyEasyPlaceConfig.placeOnTrappedChests)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnTrappedChests = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_hoppers"), LovelyEasyPlaceConfig.placeOnHoppers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnHoppers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_furnaces"), LovelyEasyPlaceConfig.placeOnFurnaces)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnFurnaces = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_smokers"), LovelyEasyPlaceConfig.placeOnSmokers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnSmokers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_blast_furnaces"), LovelyEasyPlaceConfig.placeOnBlastFurnaces)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnBlastFurnaces = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_dispensers"), LovelyEasyPlaceConfig.placeOnDispensers)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnDispensers = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_barrels"), LovelyEasyPlaceConfig.placeOnBarrels)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnBarrels = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_shulker_boxes"), LovelyEasyPlaceConfig.placeOnShulkerBoxes)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnShulkerBoxes = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_crafters"), LovelyEasyPlaceConfig.placeOnCrafters)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnCrafters = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_anvils"), LovelyEasyPlaceConfig.placeOnAnvils)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnAnvils = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_crafting_tables"), LovelyEasyPlaceConfig.placeOnCraftingTables)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnCraftingTables = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_ender_chests"), LovelyEasyPlaceConfig.placeOnEnderChests)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnEnderChests = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_looms"), LovelyEasyPlaceConfig.placeOnLooms)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnLooms = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_cartography_tables"), LovelyEasyPlaceConfig.placeOnCartographyTables)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnCartographyTables = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_grindstones"), LovelyEasyPlaceConfig.placeOnGrindstones)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnGrindstones = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_stonecutters"), LovelyEasyPlaceConfig.placeOnStonecutters)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnStonecutters = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_smithing_tables"), LovelyEasyPlaceConfig.placeOnSmithingTables)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnSmithingTables = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_brewing_stands"), LovelyEasyPlaceConfig.placeOnBrewingStands)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnBrewingStands = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_beacons"), LovelyEasyPlaceConfig.placeOnBeacons)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnBeacons = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_enchanting_tables"), LovelyEasyPlaceConfig.placeOnEnchantingTables)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnEnchantingTables = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_lecterns"), LovelyEasyPlaceConfig.placeOnLecterns)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnLecterns = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_chiseled_bookshelves"), LovelyEasyPlaceConfig.placeOnChiseledBookshelves)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnChiseledBookshelves = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_jukeboxes"), LovelyEasyPlaceConfig.placeOnJukeboxes)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnJukeboxes = value)
            .build());

        blocks.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.place_on_note_blocks"), LovelyEasyPlaceConfig.placeOnNoteBlocks)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.placeOnNoteBlocks = value)
            .build());

        ConfigCategory placement = builder.getOrCreateCategory(Text.translatable("text.lovelyeasyplace.config.placement"));

        placement.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.auto_pick_from_inventory"), LovelyEasyPlaceConfig.autoPickFromInventory)
            .setDefaultValue(true)
            .setTooltip(Text.translatable("text.lovelyeasyplace.config.auto_pick_from_inventory.tooltip"))
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.autoPickFromInventory = value)
            .build());

        placement.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.auto_rotate"), LovelyEasyPlaceConfig.autoRotate)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.autoRotate = value)
            .build());

        placement.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.reverse_placement"), LovelyEasyPlaceConfig.reversePlacement)
            .setDefaultValue(false)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.reversePlacement = value)
            .build());

        placement.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.auto_noteblock_pitch"), LovelyEasyPlaceConfig.autoNoteBlockPitch)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.autoNoteBlockPitch = value)
            .build());

        placement.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("text.lovelyeasyplace.config.match_redstone_states"), LovelyEasyPlaceConfig.matchRedstoneStates)
            .setDefaultValue(true)
            .setSaveConsumer(value -> LovelyEasyPlaceConfig.matchRedstoneStates = value)
            .build());

        builder.setSavingRunnable(() -> {
            LovelyEasyPlaceConfig.save();
            LovelyEasyPlaceMod.refreshRuntimeState();
        });

        return builder.build();
    }
}
