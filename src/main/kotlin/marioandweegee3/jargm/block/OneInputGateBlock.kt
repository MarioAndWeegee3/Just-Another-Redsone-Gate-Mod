package marioandweegee3.jargm.block

import marioandweegee3.jargm.util.OneInput
import net.minecraft.block.AbstractRedstoneGateBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class OneInputGateBlock(
    settings: Settings,
    private val action: (Boolean) -> Boolean,
): AbstractRedstoneGateBlock(settings), OneInput {
    companion object {
        val facing: DirectionProperty = FACING
        val powered: BooleanProperty = POWERED
    }

    init {
        defaultState =
            stateManager.defaultState
                .with(powered, false)
                .with(facing, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(facing, powered)
    }

    override fun getUpdateDelayInternal(state: BlockState): Int = 2

    override fun hasPower(world: World, pos: BlockPos, state: BlockState): Boolean {
        return action(super.hasPower(world, pos, state))
    }
}