package com.tweaks.twoinone.mixin;

import com.tweaks.twoinone.Tweaks2in1.Freecam;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    // Keep your real player still while the camera flies around.
    @Inject(method = "tick", at = @At("TAIL"))
    private void tweaks$freeze(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (!Freecam.active) return;
        pressingForward = pressingBack = pressingLeft = pressingRight = false;
        jumping = false;
        sneaking = false;
        movementForward = 0f;
        movementSideways = 0f;
    }
}
