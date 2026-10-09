package com.tweaks.twoinone.mixin;

import com.tweaks.twoinone.Tweaks2in1.XRay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> {
    // Fullbright while X-Ray is on: report a very high gamma.
    @SuppressWarnings("unchecked")
    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void tweaks$fullbright(CallbackInfoReturnable<T> cir) {
        if (!XRay.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions opts = mc == null ? null : mc.options;
        if (opts != null && (Object) this == opts.getGamma()) {
            cir.setReturnValue((T) Double.valueOf(16.0));
        }
    }
}
