package com.lovelyeasyplace.integration;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Method;

/**
 * Soft integration adapter for Litematica schematic querying.
 * Uses reflection so Litematica is strictly an optional dependency.
 */
public class LitematicaAdapter {

    private static Boolean litematicaAvailable = null;
    private static boolean reflectionInitialized = false;

    // Reflection method caches
    private static Method getSchematicWorldFromHandler;
    private static Method getPlacementManager;
    private static Method getSchematicWorldFromManager;
    private static Method getPlacementAt;
    private static Method getPlacementOf;
    private static Method getSchematicStateFromPlacement;

    public static boolean isAvailable() {
        if (litematicaAvailable == null) {
            litematicaAvailable = FabricLoader.getInstance().isModLoaded("litematica");
        }
        return litematicaAvailable;
    }

    private static boolean isPlacementEnabledReflectively(Object placement) {
        try {
            Method m = placement.getClass().getMethod("isEnabled");
            Object result = m.invoke(placement);
            if (result instanceof Boolean b) {
                return b;
            }
        } catch (Exception ignored) {}
        return true;
    }

    public static boolean isLitematicaRenderingEnabled() {
        if (!isAvailable()) {
            return false;
        }
        try {
            Class<?> configsClass = Class.forName("fi.dy.masa.litematica.config.Configs$Visuals");
            java.lang.reflect.Field field = configsClass.getField("ENABLE_RENDERING");
            Object configValue = field.get(null);
            if (configValue != null) {
                Method getBooleanValue = configValue.getClass().getMethod("getBooleanValue");
                return (Boolean) getBooleanValue.invoke(configValue);
            }
        } catch (Exception ignored) {}
        return true;
    }

    /**
     * Attempts to query Litematica's active schematic placement state at the given block position.
     *
     * @param world client world
     * @param pos block position
     * @return schematic BlockState if available and non-air, otherwise null.
     */
    public static BlockState getSchematicState(World world, BlockPos pos) {
        if (!isAvailable() || pos == null) {
            return null;
        }

        initializeReflection();

        // 1. Try SchematicWorldHandler.getSchematicWorld() -> getBlockState(pos) (primary Litematica world path)
        if (getSchematicWorldFromHandler != null) {
            try {
                Object schematicWorld = getSchematicWorldFromHandler.invoke(null);
                if (schematicWorld != null) {
                    BlockState state = getBlockStateReflectively(schematicWorld, pos);
                    if (state != null && !state.isAir()) {
                        return state;
                    }
                }
            } catch (Exception ignored) {}
        }

        // 2. Try DataManager placement manager lookup if available
        if (getPlacementManager != null) {
            try {
                Object mgr = getPlacementManager.invoke(null);
                if (mgr != null) {
                    Method placementMethod = getPlacementAt != null ? getPlacementAt : getPlacementOf;
                    if (placementMethod != null) {
                        Object placement = placementMethod.invoke(mgr, pos);
                        if (placement != null) {
                            if (!isPlacementEnabledReflectively(placement)) {
                                return null; // placement is disabled
                            }
                            BlockState state = getSchematicStateReflectively(placement, pos);
                            if (state != null && !state.isAir()) {
                                return state;
                            }
                        }
                    } else if (getSchematicWorldFromManager != null) {
                        Object schematicWorld = getSchematicWorldFromManager.invoke(mgr);
                        if (schematicWorld != null) {
                            BlockState state = getBlockStateReflectively(schematicWorld, pos);
                            if (state != null && !state.isAir()) {
                                return state;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    private static BlockState getBlockStateReflectively(Object obj, BlockPos pos) {
        if (obj instanceof World w) {
            return w.getBlockState(pos);
        }
        try {
            Method m = obj.getClass().getMethod("getBlockState", BlockPos.class);
            Object result = m.invoke(obj, pos);
            if (result instanceof BlockState bs) {
                return bs;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static BlockState getSchematicStateReflectively(Object placement, BlockPos pos) {
        try {
            Method m = placement.getClass().getMethod("getSchematicState", BlockPos.class);
            Object result = m.invoke(placement, pos);
            if (result instanceof BlockState bs) {
                return bs;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static void initializeReflection() {
        if (reflectionInitialized) {
            return;
        }
        reflectionInitialized = true;

        // Try 1: fi.dy.masa.litematica.world.SchematicWorldHandler.getSchematicWorld()
        try {
            Class<?> handlerClass = Class.forName("fi.dy.masa.litematica.world.SchematicWorldHandler");
            getSchematicWorldFromHandler = handlerClass.getMethod("getSchematicWorld");
        } catch (Exception e) {
            logDiscoveryFailure("SchematicWorldHandler.getSchematicWorld", e);
        }

        // Try 2: fi.dy.masa.litematica.data.DataManager.getSchematicPlacementManager()
        try {
            Class<?> dataMgrClass = Class.forName("fi.dy.masa.litematica.data.DataManager");
            getPlacementManager = dataMgrClass.getMethod("getSchematicPlacementManager");
            if (getPlacementManager != null) {
                Class<?> placementMgrClass = getPlacementManager.getReturnType();
                try {
                    getSchematicWorldFromManager = placementMgrClass.getMethod("getSchematicWorld");
                } catch (Exception ignored) {}

                try {
                    getPlacementAt = placementMgrClass.getMethod("getPlacementAt", BlockPos.class);
                } catch (Exception ignored) {}

                try {
                    getPlacementOf = placementMgrClass.getMethod("getSchematicPlacementOf", BlockPos.class);
                } catch (Exception ignored) {}

                Method testPlacementMethod = getPlacementAt != null ? getPlacementAt : getPlacementOf;
                if (testPlacementMethod != null) {
                    Class<?> placementClass = testPlacementMethod.getReturnType();
                    try {
                        getSchematicStateFromPlacement = placementClass.getMethod("getSchematicState", BlockPos.class);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            logDiscoveryFailure("DataManager.getSchematicPlacementManager", e);
        }
    }

    private static void logDiscoveryFailure(String target, Exception e) {
        LovelyEasyPlaceMod.LOGGER.warn("Could not resolve Litematica integration method {}: {}", target, e.getMessage());
    }
}

