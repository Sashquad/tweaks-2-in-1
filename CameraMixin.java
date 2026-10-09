package com.tweaks.twoinone.mixin;

import com.tweaks.twoinone.Tweaks2in1.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow private boolean thirdPerson;

    @Inject(method = "update", at = @At("TAIL"))
    private void tweaks$freecam(BlockView area, Entity focused, boolean thirdPerson,
                                boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!Freecam.active) return;
        setRotation(Freecam.yaw, Freecam.pitch);
        setPos(Freecam.px + (Freecam.x - Freecam.px) * tickDelta,
               Freecam.py + (Freecam.y - Freecam.py) * tickDelta,
               Freecam.pz + (Freecam.z - Freecam.pz) * tickDelta);
        this.thirdPerson = true; // so your body is rendered while the camera roams
    }
}
