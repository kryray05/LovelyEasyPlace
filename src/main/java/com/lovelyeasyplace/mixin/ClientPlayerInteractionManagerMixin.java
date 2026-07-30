package com.lovelyeasyplace.mixin;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import com.lovelyeasyplace.config.LovelyEasyPlaceConfig;
import com.lovelyeasyplace.integration.LitematicaAdapter;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    private Item lovelyeasyplace$heldBefore = null;
    private Float lovelyeasyplace$originalYaw = null;
    private Float lovelyeasyplace$originalPitch = null;
    private static boolean isAdjustingState = false;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlockHead(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (!LovelyEasyPlaceMod.isEnabled() || isAdjustingState) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        // 1. Check if manually right-clicking an existing Note Block to tune it
        if (!player.isSneaking() && LovelyEasyPlaceConfig.autoNoteBlockPitch && !(player.getStackInHand(hand).getItem() instanceof BlockItem)) {
            BlockPos clickedPos = hitResult.getBlockPos();
            BlockState clickedState = player.getEntityWorld().getBlockState(clickedPos);
            if (clickedState.getBlock() instanceof NoteBlock) {
                if (adjustNoteBlock(client, player, hand, clickedPos, hitResult)) {
                    cir.setReturnValue(ActionResult.SUCCESS);
                    return;
                }
            }
        }

        // 2. Sneak-faking
        if (this.shouldStartEasyPlace(player, hand, hitResult)) {
            LovelyEasyPlaceMod.beginPlacementSneak(player);
        }

        // 3. Rotation spoofing for block placement
        ItemStack stack = player.getStackInHand(hand);
        lovelyeasyplace$heldBefore = stack.getItem();

        if (lovelyeasyplace$heldBefore instanceof BlockItem blockItem) {
            BlockPos clickedPos = hitResult.getBlockPos();
            BlockState clickedState = player.getEntityWorld().getBlockState(clickedPos);
            BlockPos placedPos = clickedState.isReplaceable() ? clickedPos : clickedPos.offset(hitResult.getSide());

            Direction targetFacing = null;

            // Check Litematica schematic facing
            if (LovelyEasyPlaceConfig.autoRotate) {
                BlockState schematic = LitematicaAdapter.getSchematicState(player.getEntityWorld(), placedPos);
                if (schematic != null && !schematic.isAir()) {
                    targetFacing = extractFacing(schematic);
                }
            }

            // Check reverse placement
            if (targetFacing == null && LovelyEasyPlaceConfig.reversePlacement) {
                targetFacing = player.getHorizontalFacing().getOpposite();
            }

            if (targetFacing != null) {
                float[] angles = getRequiredYawAndPitch(targetFacing, blockItem.getBlock(), player);
                float[] aligned = alignToGCD(client, angles[0], angles[1]);

                if (player.networkHandler != null) {
                    player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                        aligned[0], aligned[1], player.isOnGround(), player.horizontalCollision
                    ));
                }

                lovelyeasyplace$originalYaw = player.getYaw();
                lovelyeasyplace$originalPitch = player.getPitch();

                // Set client player local rotation so placement prediction uses spoofed facing (prevents prediction flicker)
                player.setYaw(aligned[0]);
                player.setPitch(aligned[1]);
            }
        }
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void onInteractBlockReturn(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (isAdjustingState) {
            return;
        }

        // Restore rotation
        if (lovelyeasyplace$originalYaw != null && lovelyeasyplace$originalPitch != null) {
            if (player.networkHandler != null) {
                MinecraftClient client = MinecraftClient.getInstance();
                float[] alignedOriginal = alignToGCD(client, lovelyeasyplace$originalYaw, lovelyeasyplace$originalPitch);
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                    alignedOriginal[0], alignedOriginal[1], player.isOnGround(), player.horizontalCollision
                ));
            }
            player.setYaw(lovelyeasyplace$originalYaw);
            player.setPitch(lovelyeasyplace$originalPitch);
            lovelyeasyplace$originalYaw = null;
            lovelyeasyplace$originalPitch = null;
        }

        // End sneak-faking
        LovelyEasyPlaceMod.endPlacementSneak(player);

        // Auto-tune placed note block or redstone component
        if (cir.getReturnValue() != null && cir.getReturnValue().isAccepted()) {
            if (lovelyeasyplace$heldBefore instanceof BlockItem blockItem) {
                BlockPos clickedPos = hitResult.getBlockPos();
                BlockState clickedState = player.getEntityWorld().getBlockState(clickedPos);
                BlockPos placedPos = clickedState.isReplaceable() ? clickedPos : clickedPos.offset(hitResult.getSide());

                MinecraftClient client = MinecraftClient.getInstance();
                BlockHitResult newHit = new BlockHitResult(
                    Vec3d.ofCenter(placedPos),
                    Direction.UP,
                    placedPos,
                    false
                );
                if (LovelyEasyPlaceConfig.autoNoteBlockPitch && blockItem.getBlock() instanceof NoteBlock) {
                    adjustNoteBlock(client, player, hand, placedPos, newHit);
                } else if (LovelyEasyPlaceConfig.matchRedstoneStates && (blockItem.getBlock() instanceof RepeaterBlock || blockItem.getBlock() instanceof ComparatorBlock)) {
                    adjustRedstoneComponent(client, player, hand, placedPos, newHit);
                }
            }
        }

        lovelyeasyplace$heldBefore = null;
    }

    private boolean adjustNoteBlock(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockPos pos, BlockHitResult hitResult) {
        BlockState schematic = LitematicaAdapter.getSchematicState(player.getEntityWorld(), pos);
        if (schematic == null || !(schematic.getBlock() instanceof NoteBlock)) return false;

        BlockState current = player.getEntityWorld().getBlockState(pos);
        int currentNote = 0;
        if (current.getBlock() instanceof NoteBlock && current.contains(NoteBlock.NOTE)) {
            currentNote = current.get(NoteBlock.NOTE);
        }

        if (schematic.contains(NoteBlock.NOTE)) {
            int targetNote = schematic.get(NoteBlock.NOTE);
            int clicksNeeded = (targetNote - currentNote + 25) % 25;
            if (clicksNeeded > 0) {
                // Instead of a direct loop, queue the clicks one tick at a time
                for (int i = 0; i < clicksNeeded; i++) {
                    LovelyEasyPlaceMod.clickQueue.add(() -> {
                        if (LovelyEasyPlaceMod.isEnabled() && client.player != null && client.interactionManager != null) {
                            isAdjustingState = true;
                            try {
                                client.interactionManager.interactBlock(client.player, hand, hitResult);
                            } finally {
                                isAdjustingState = false;
                            }
                        }
                    });
                }
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean adjustRedstoneComponent(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockPos pos, BlockHitResult hitResult) {
        BlockState schematic = LitematicaAdapter.getSchematicState(player.getEntityWorld(), pos);
        if (schematic == null) return false;

        BlockState current = player.getEntityWorld().getBlockState(pos);
        boolean adjusted = false;

        if (schematic.getBlock() instanceof RepeaterBlock && current.getBlock() instanceof RepeaterBlock) {
            if (schematic.contains(RepeaterBlock.DELAY) && current.contains(RepeaterBlock.DELAY)) {
                int targetDelay = schematic.get(RepeaterBlock.DELAY);
                int currentDelay = current.get(RepeaterBlock.DELAY);
                int clicksNeeded = (targetDelay - currentDelay + 4) % 4;
                if (clicksNeeded > 0) {
                    for (int i = 0; i < clicksNeeded; i++) {
                        // Queue repeater clicks
                        LovelyEasyPlaceMod.clickQueue.add(() -> {
                            if (LovelyEasyPlaceMod.isEnabled() && client.player != null && client.interactionManager != null) {
                                isAdjustingState = true;
                                try {
                                    client.interactionManager.interactBlock(client.player, hand, hitResult);
                                } finally {
                                    isAdjustingState = false;
                                }
                            }
                        });
                    }
                    adjusted = true;
                }
            }
        } else if (schematic.getBlock() instanceof ComparatorBlock && current.getBlock() instanceof ComparatorBlock) {
            if (schematic.contains(ComparatorBlock.MODE) && current.contains(ComparatorBlock.MODE)) {
                net.minecraft.block.enums.ComparatorMode targetMode = schematic.get(ComparatorBlock.MODE);
                net.minecraft.block.enums.ComparatorMode currentMode = current.get(ComparatorBlock.MODE);
                if (targetMode != currentMode) {
                    LovelyEasyPlaceMod.clickQueue.add(() -> {
                        if (LovelyEasyPlaceMod.isEnabled() && client.player != null && client.interactionManager != null) {
                            isAdjustingState = true;
                            try {
                                client.interactionManager.interactBlock(client.player, hand, hitResult);
                            } finally {
                                isAdjustingState = false;
                            }
                        }
                    });
                    adjusted = true;
                }
            }
        }
        return adjusted;
    }

    private boolean shouldStartEasyPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof BlockItem)) {
            return false;
        }
        World world = player.getEntityWorld();
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        return this.shouldFakeSneakForBlock(state.getBlock());
    }

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
        return block instanceof NoteBlock && LovelyEasyPlaceConfig.placeOnNoteBlocks;
    }

    private static Direction extractFacing(BlockState state) {
        if (state == null) return null;
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            return state.get(Properties.HORIZONTAL_FACING);
        }
        if (state.contains(Properties.FACING)) {
            return state.get(Properties.FACING);
        }
        if (state.contains(Properties.HOPPER_FACING)) {
            return state.get(Properties.HOPPER_FACING);
        }
        if (state.contains(Properties.ORIENTATION)) {
            return state.get(Properties.ORIENTATION).getFacing();
        }
        return null;
    }

    private static float[] getRequiredYawAndPitch(Direction targetFacing, Block block, ClientPlayerEntity player) {
        if (targetFacing == null) {
            return new float[]{player.getYaw(), player.getPitch()};
        }

        float yaw = player.getYaw();
        float currentPitch = player.getPitch();
        float pitch = net.minecraft.util.math.MathHelper.clamp(currentPitch, -45.0f, 45.0f);

        if (targetFacing == Direction.UP) {
            if (block instanceof ObserverBlock
                    || block instanceof PistonBlock
                    || block instanceof DispenserBlock
                    || block instanceof BarrelBlock
                    || block instanceof CrafterBlock) {
                pitch = 90f;
            } else {
                pitch = -90f;
            }
        } else if (targetFacing == Direction.DOWN) {
            if (block instanceof ObserverBlock
                    || block instanceof PistonBlock
                    || block instanceof DispenserBlock
                    || block instanceof BarrelBlock
                    || block instanceof CrafterBlock) {
                pitch = -90f;
            } else {
                pitch = 90f;
            }
        } else {
            if (block instanceof RepeaterBlock
                    || block instanceof ComparatorBlock
                    || block instanceof StairsBlock
                    || block instanceof DoorBlock
                    || block instanceof BedBlock) {
                switch (targetFacing) {
                    case NORTH -> yaw = 180f;
                    case SOUTH -> yaw = 0f;
                    case WEST -> yaw = 90f;
                    case EAST -> yaw = -90f;
                    default -> yaw = 0f;
                }
            } else if (block instanceof AnvilBlock) {
                switch (targetFacing) {
                    case NORTH -> yaw = 90f;
                    case EAST -> yaw = 180f;
                    case SOUTH -> yaw = -90f;
                    case WEST -> yaw = 0f;
                    default -> yaw = 0f;
                }
            } else {
                switch (targetFacing) {
                    case NORTH -> yaw = 0f;
                    case SOUTH -> yaw = 180f;
                    case WEST -> yaw = -90f;
                    case EAST -> yaw = 90f;
                    default -> yaw = 0f;
                }
            }
        }

        return new float[]{yaw, pitch};
    }

    private static float[] alignToGCD(MinecraftClient client, float yaw, float pitch) {
        if (client == null || client.options == null) {
            return new float[]{yaw, pitch};
        }
        double sensitivity = client.options.getMouseSensitivity().getValue();
        double f = sensitivity * 0.6D + 0.2D;
        double g = f * f * f * 8.0D;
        double step = g * 0.15D;

        if (client.player != null) {
            float currentYaw = client.player.getYaw();
            float currentPitch = client.player.getPitch();

            float diffYaw = yaw - currentYaw;
            long stepsYaw = Math.round(diffYaw / step);
            float alignedYaw = currentYaw + (float)(stepsYaw * step);

            float diffPitch = pitch - currentPitch;
            long stepsPitch = Math.round(diffPitch / step);
            float alignedPitch = currentPitch + (float)(stepsPitch * step);

            return new float[]{alignedYaw, alignedPitch};
        }

        return new float[]{yaw, pitch};
    }
}
