package com.lovelyeasyplace.integration;

import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Integration with ModMenu for configuration GUI.
 * This allows users to configure the mod through ModMenu's interface.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return LovelyEasyPlaceConfig::createConfigScreen;
    }
}
