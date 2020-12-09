package marioandweegee3.jargm.mixin;

import marioandweegee3.jargm.util.RedstoneWireConnectionCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireMixin {
    @Inject(at = @At("TAIL"), method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
    private static void jargm_canConnect(BlockState state, @Nullable Direction direction, CallbackInfoReturnable<Boolean> ci) {
        RedstoneWireConnectionCallback.INSTANCE.canConnect(state, direction, ci);
    }
}
