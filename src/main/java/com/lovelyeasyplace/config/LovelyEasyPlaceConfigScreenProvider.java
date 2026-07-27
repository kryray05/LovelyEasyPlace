package com.lovelyeasyplace.config;

import net.minecraft.client.gui.screen.Screen;

public class LovelyEasyPlaceConfigScreenProvider {
    public static Screen createScreen(Screen parent) {
        if (isClothConfigPresent()) {
            try {
                return ClothConfigHolder.create(parent);
            } catch (Throwable t) {
                return new LovelyEasyPlaceFallbackConfigScreen(parent);
            }
        } else {
            return new LovelyEasyPlaceFallbackConfigScreen(parent);
        }
    }

    private static boolean isLunarClient() {
        try {
            String brand = net.minecraft.client.ClientBrandRetriever.getClientModName();
            if (brand != null && brand.toLowerCase().contains("lunar")) {
                return true;
            }
        } catch (Throwable ignored) {}
        try {
            Class.forName("com.moonsworth.lunar.genesis.Genesis");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.moonsworth.lunar.client.LunarClient");
                return true;
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }

    private static boolean isClothConfigPresent() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // Nested class to prevent verification of Cloth Config classes when it is absent
    private static class ClothConfigHolder {
        public static Screen create(Screen parent) {
            return ClothConfigScreenCreator.create(parent);
        }
    }
}
