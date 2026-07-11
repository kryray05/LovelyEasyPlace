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

        return shouldFakeSneakForBlock(state.getBlock());
    }

    /**
     * Check if we should fake sneak for the given block.
     * This determines which blocks trigger the fake sneak behavior.
     */
    private boolean shouldFakeSneakForBlock(Block block) {
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
        return false;
    }
}
