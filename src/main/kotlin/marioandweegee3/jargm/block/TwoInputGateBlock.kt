package marioandweegee3.jargm.block

import marioandweegee3.jargm.util.TwoInput
import marioandweegee3.jargm.util.getPowerIn
import net.minecraft.block.AbstractRedstoneGateBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class TwoInputGateBlock(
    settings: Settings,
    private val action: (Boolean, Boolean) -> Boolean
): AbstractRedstoneGateBlock(settings), TwoInput {
    override fun getUpdateDelayInternal(state: BlockState): Int = 2

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
        builder.add(powered, facing)
    }

    private fun getLeftPower(world: World, pos: BlockPos, state: BlockState): Int {
        val left = state[facing].rotateYClockwise()

        return getPowerIn(world, pos, left)
    }

    private fun hasLeftPower(world: World, pos: BlockPos, state: BlockState): Boolean =
        getLeftPower(world, pos, state) > 0

    private fun getRightPower(world: World, pos: BlockPos, state: BlockState): Int {
        val right = state[facing].rotateYCounterclockwise()

        return getPowerIn(world, pos, right)
    }

    private fun hasRightPower(world: World, pos: BlockPos, state: BlockState): Boolean =
        getRightPower(world, pos, state) > 0

    override fun hasPower(world: World, pos: BlockPos, state: BlockState): Boolean {
        return action(hasLeftPower(world, pos, state), hasRightPower(world, pos, state))
    }
}