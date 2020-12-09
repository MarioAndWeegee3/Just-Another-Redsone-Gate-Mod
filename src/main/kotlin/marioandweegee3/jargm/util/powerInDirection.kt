package marioandweegee3.jargm.util

import net.minecraft.block.RedstoneWireBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.max

fun getPowerIn(world: World, pos: BlockPos, direction: Direction): Int {
    val searchPos = pos.offset(direction)
    val worldPower = world.getEmittedRedstonePower(searchPos, direction)

    return when {
        worldPower >= 15 -> worldPower
        else -> {
            val searchState = world.getBlockState(searchPos)
            max(
                worldPower,
                if (searchState.block is RedstoneWireBlock)
                    searchState[RedstoneWireBlock.POWER]
                else
                    0
            )
        }
    }
}