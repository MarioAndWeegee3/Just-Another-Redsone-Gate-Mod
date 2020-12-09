package marioandweegee3.jargm.util

import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.util.math.Direction
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object RedstoneWireConnectionCallback {
    fun canConnect(state: BlockState, direction: Direction?, ci: CallbackInfoReturnable<Boolean>) {
        when (val block = state.block) {
            is OneInput -> {
                val facing = state[HorizontalFacingBlock.FACING]
                ci.returnValue = when (direction) {
                    facing, facing.opposite -> true
                    else -> false
                }
            }
            is TwoInput -> {
                val facing = state[HorizontalFacingBlock.FACING]
                if (direction == facing.opposite)
                    ci.returnValue = false
            }
        }
    }
}