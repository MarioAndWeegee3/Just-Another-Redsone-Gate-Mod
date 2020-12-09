package marioandweegee3.jargm.block

import marioandweegee3.jargm.util.getPowerIn
import net.minecraft.block.AbstractRedstoneGateBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.TickPriority
import net.minecraft.world.World
import java.util.*

class TwoInTwoOutGateBlock(
    settings: Settings,
    private val action: (Boolean, Boolean) -> Pair<Boolean, Boolean>,
): AbstractRedstoneGateBlock(settings) {
    companion object {
        /// This corresponds to the Front output (facing.opposite)
        val outputA: BooleanProperty = BooleanProperty.of("output_a")

        /// This corresponds to the Left output
        val outputB: BooleanProperty = BooleanProperty.of("output_b")

        val facing: DirectionProperty = FACING
    }

    init {
        defaultState =
            stateManager.defaultState
                .with(outputA, false)
                .with(outputB, false)
                .with(facing, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(outputA, outputB, facing)
    }

    override fun getUpdateDelayInternal(state: BlockState?): Int = 2

    override fun hasPower(world: World, pos: BlockPos, state: BlockState): Boolean {
        return hasRearPower(world, pos, state) || hasRightPower(world, pos, state)
    }

    private fun getRightPower(world: World, pos: BlockPos, state: BlockState): Int {
        val left = state[facing].rotateYCounterclockwise()

        return getPowerIn(world, pos, left)
    }

    private fun hasRightPower(world: World, pos: BlockPos, state: BlockState): Boolean =
        getRightPower(world, pos, state) > 0

    private fun getRearPower(world: World, pos: BlockPos, state: BlockState): Int {
        val rear = state[facing]

        return getPowerIn(world, pos, rear)
    }

    private fun hasRearPower(world: World, pos: BlockPos, state: BlockState): Boolean =
        getRearPower(world, pos, state) > 0

    private fun getOutPower(world: World, pos: BlockPos, state: BlockState): Pair<Boolean, Boolean> {
        val left = hasRightPower(world, pos, state)
        val rear = hasRearPower(world, pos, state)

        return action(left, rear)
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val (isAPowered, isBPowered) = state[outputA] to state[outputB]
        val (aHasPower, bHasPower) = getOutPower(world, pos, state)

        val newState = run {
            var s = state

            if (isAPowered && !aHasPower)
                s = s.with(outputA, false)
            else if (!isAPowered) {
                if (!aHasPower)
                    world.blockTickScheduler.schedule(pos, this, 2, TickPriority.VERY_HIGH)
                else
                    s = s.with(outputA, true)
            }

            if (isBPowered && !bHasPower)
                s = s.with(outputB, false)
            else if (!isBPowered) {
                if (!bHasPower)
                    world.blockTickScheduler.schedule(pos, this, 2, TickPriority.VERY_HIGH)
                else
                    s = s.with(outputB, true)
            }

            s
        }

        world.setBlockState(pos, newState, 2)
    }

    override fun getWeakRedstonePower(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        direction: Direction
    ): Int {
        if (!state[outputA] && !state[outputB])
            return 0

        val leftDir = state[facing].rotateYClockwise().opposite
        val frontDir = state[facing]

        return when (direction) {
            frontDir -> if (state[outputA]) 15 else 0
            leftDir -> if (state[outputB]) 15 else 0
            else -> 0
        }
    }

    override fun updatePowered(world: World, pos: BlockPos, state: BlockState) {
        val isPowered = state[outputA] to state[outputB]
        val hasPower = getOutPower(world, pos, state)

        val tickScheduler = world.blockTickScheduler

        if (isPowered != hasPower && !tickScheduler.isTicking(pos, this)) {
            val priority = if (isTargetNotAligned(world, pos, state))
                TickPriority.EXTREMELY_HIGH
            else TickPriority.VERY_HIGH

            world.blockTickScheduler.schedule(pos, this, 2, priority)
        }
    }

    override fun updateTarget(world: World, pos: BlockPos, state: BlockState) {
        val leftDir = state[facing].rotateYClockwise()
        val frontDir = state[facing].opposite

        val positions = listOf(
            pos.offset(leftDir) to leftDir,
            pos.offset(frontDir) to frontDir,
        )

        for ((p, d) in positions) {
            world.updateNeighbor(p, this, pos)
            world.updateNeighborsExcept(p, this, d.opposite)
        }
    }

    override fun isTargetNotAligned(world: BlockView, pos: BlockPos, state: BlockState): Boolean {
        val leftDir = state[facing].rotateYClockwise()
        val frontDir = state[facing].opposite

        val rightState = world.getBlockState(pos.offset(leftDir))
        val frontState = world.getBlockState(pos.offset(frontDir))

        fun isNotAligned(state: BlockState, direction: Direction): Boolean =
            isRedstoneGate(state) && state[facing] != direction

        return isNotAligned(rightState, leftDir) || isNotAligned(frontState, frontDir)
    }
}