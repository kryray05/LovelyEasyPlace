package com.lovelyeasyplace.mixin;

import com.lovelyeasyplace.LovelyEasyPlaceMod;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for ClientPlayerEntity.
 * Fakes local sneaking state while LovelyEasyPlace is placing a block.
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    /**
     * Inject into isSneaking to return true when fake sneak is enabled.
     * The server state is handled by vanilla player-input packets from the main mod.
     */
    @Inject(
        method = "isSneaking",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onIsSneaking(CallbackInfoReturnable<Boolean> cir) {
        if (LovelyEasyPlaceMod.shouldFakeSneak()) {
            cir.setReturnValue(true);
        }
    }
}
