package com.tweaks.twoinone.mixin;

import com.tweaks.twoinone.Tweaks2in1.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    // Mouse movement rotates the free camera instead of your player.
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void tweaks$look(double dx, double dy, CallbackInfo ci) {
        if (Freecam.active && (Object) this == MinecraftClient.getInstance().player) {
            Freecam.look(dx, dy);
            ci.cancel();
        }
    }
}
