package com.lovelyeasyplace.mixin;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlastFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SmokerBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for ClientPlayerInteractionManager.
 * Intercepts placement against configured interactive blocks.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    /**
     * Inject into interactBlock to enable fake sneak before interaction
     * if the target block is one we want to place on.
     */
    @Inject(
        method = "interactBlock",
        at = @At("HEAD")
    )
    private void onInteractBlockHead(
        ClientPlayerEntity player,
        Hand hand,
        BlockHitResult hitResult,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!LovelyEasyPlaceMod.isEnabled()) {
            return;
        }

        if (shouldStartEasyPlace(player, hand, hitResult)) {
            LovelyEasyPlaceMod.beginPlacementSneak(player);
        }
    }

    /**
     * Inject at the end of interactBlock to disable fake sneak.
     */
    @Inject(
        method = "interactBlock",
        at = @At("RETURN")
    )
    private void onInteractBlockReturn(
        ClientPlayerEntity player,
        Hand hand,
        BlockHitResult hitResult,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        LovelyEasyPlaceMod.endPlacementSneak(player);
    }

    private boolean shouldStartEasyPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof BlockItem)) {
            return false;
        }

        World world = player.getEntityWorld();
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);

        return shouldFakeSneakForBlock(state, world, pos);
    }

    /**
     * Check if we should fake sneak for the given block.
     * This determines which blocks trigger the fake sneak behavior.
     */
    private boolean shouldFakeSneakForBlock(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof TrappedChestBlock && LovelyEasyPlaceConfig.placeOnTrappedChests) {
            return true;
        }
        if (block instanceof ChestBlock && LovelyEasyPlaceConfig.placeOnChests) {
            return true;
        }
        if (block instanceof HopperBlock && LovelyEasyPlaceConfig.placeOnHoppers) {
            return true;
        }
        if (block instanceof FurnaceBlock && LovelyEasyPlaceConfig.placeOnFurnaces) {
            return true;
        }
        if (block instanceof DispenserBlock && LovelyEasyPlaceConfig.placeOnDispensers) {
            return true;
        }
        if (block instanceof DropperBlock && LovelyEasyPlaceConfig.placeOnDroppers) {
            return true;
        }
        if (block instanceof BarrelBlock && LovelyEasyPlaceConfig.placeOnBarrels) {
            return true;
        }
        if (block instanceof ShulkerBoxBlock && LovelyEasyPlaceConfig.placeOnShulkerBoxes) {
            return true;
        }
        if (block instanceof SmokerBlock && LovelyEasyPlaceConfig.placeOnSmokers) {
            return true;
        }
        if (block instanceof BlastFurnaceBlock && LovelyEasyPlaceConfig.placeOnBlastFurnaces) {
            return true;
        }
        if (block instanceof CrafterBlock && LovelyEasyPlaceConfig.placeOnCrafters) {
            return true;
        }
        if (block instanceof AnvilBlock && LovelyEasyPlaceConfig.placeOnAnvils) {
            return true;
        }
        if (block instanceof CraftingTableBlock && LovelyEasyPlaceConfig.placeOnCraftingTables) {
            return true;
        }
        if (block instanceof EnderChestBlock && LovelyEasyPlaceConfig.placeOnEnderChests) {
            return true;
        }
        if (block instanceof LoomBlock && LovelyEasyPlaceConfig.placeOnLooms) {
            return true;
        }
        if (block instanceof CartographyTableBlock && LovelyEasyPlaceConfig.placeOnCartographyTables) {
            return true;
        }
        if (block instanceof GrindstoneBlock && LovelyEasyPlaceConfig.placeOnGrindstones) {
            return true;
        }
        if (block instanceof StonecutterBlock && LovelyEasyPlaceConfig.placeOnStonecutters) {
            return true;
        }
        if (block instanceof SmithingTableBlock && LovelyEasyPlaceConfig.placeOnSmithingTables) {
            return true;
        }
        if (block instanceof BrewingStandBlock && LovelyEasyPlaceConfig.placeOnBrewingStands) {
            return true;
        }
        if (block instanceof BeaconBlock && LovelyEasyPlaceConfig.placeOnBeacons) {
            return true;
        }
        if (block instanceof EnchantingTableBlock && LovelyEasyPlaceConfig.placeOnEnchantingTables) {
            return true;
        }
        if (block instanceof LecternBlock && LovelyEasyPlaceConfig.placeOnLecterns) {
            return true;
        }
        if (block instanceof ChiseledBookshelfBlock && LovelyEasyPlaceConfig.placeOnChiseledBookshelves) {
            return true;
        }
        if (block instanceof JukeboxBlock && LovelyEasyPlaceConfig.placeOnJukeboxes) {
            return true;
        }
        if (block instanceof NoteBlock && LovelyEasyPlaceConfig.placeOnNoteBlocks) {
            return true;
        }
        if (block instanceof DoorBlock && LovelyEasyPlaceConfig.placeOnDoors) {
            return true;
        }
        if (block instanceof TrapdoorBlock && LovelyEasyPlaceConfig.placeOnTrapdoors) {
            return true;
        }
        if (block instanceof FenceGateBlock && LovelyEasyPlaceConfig.placeOnFenceGates) {
            return true;
        }
        if (block instanceof AbstractRedstoneGateBlock && LovelyEasyPlaceConfig.placeOnRepeatersComparators) {
            return true;
        }
        if ((block instanceof LeverBlock || block instanceof AbstractButtonBlock) && LovelyEasyPlaceConfig.placeOnLeversButtons) {
            return true;
        }

        // Auto-detect any GUI-opening blocks (e.g. modded containers, custom tables, etc.)
        if (LovelyEasyPlaceConfig.autoDetectGuiBlocks) {
            try {
                if (state.createScreenHandlerFactory(world, pos) != null) {
                    return true;
                }
            } catch (Exception ignored) {
                // Safety fallback for unexpected client-side issues
            }
        }
        return false;
    }
}
