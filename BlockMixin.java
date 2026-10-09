package com.tweaks.twoinone.mixin;

import com.tweaks.twoinone.Tweaks2in1.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void tweaks$xray(BlockState state, BlockView world, BlockPos pos,
                                    Direction side, BlockPos otherPos,
                                    CallbackInfoReturnable<Boolean> cir) {
        if (XRay.enabled) cir.setReturnValue(XRay.isVisible(state));
    }
}
