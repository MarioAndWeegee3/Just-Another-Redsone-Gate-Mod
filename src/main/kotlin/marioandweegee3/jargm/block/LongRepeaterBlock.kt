package marioandweegee3.jargm.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.RepeaterBlock

object LongRepeaterBlock: RepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER)) {
    override fun getUpdateDelayInternal(state: BlockState): Int {
        return super.getUpdateDelayInternal(state) * 4
    }
}