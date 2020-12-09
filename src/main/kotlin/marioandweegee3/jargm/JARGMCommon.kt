package marioandweegee3.jargm

import marioandweegee3.jargm.block.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class JARGMCommon: ModInitializer {
    companion object {
        const val modID = "jargm"
        val logger: Logger = LogManager.getLogger("Just Another Redstone Gate Mod")
    }

    override fun onInitialize() {
        logger.info("Initializing...")

        val gateBlockSettings = FabricBlockSettings.copy(Blocks.REPEATER)
        val gateItemSettings = Item.Settings().group(ItemGroup.REDSTONE)

        val gateBlocks = mapOf(
            Identifier(modID, "and_gate")
                to TwoInputGateBlock(gateBlockSettings, Boolean::and),
            Identifier(modID, "nand_gate")
                to TwoInputGateBlock(gateBlockSettings) { l, r -> !(l and r) },
            Identifier(modID, "xor_gate")
                to TwoInputGateBlock(gateBlockSettings, Boolean::xor),
            Identifier(modID, "xnor_gate")
                to TwoInputGateBlock(gateBlockSettings, Boolean::equals),
            Identifier(modID, "or_gate")
                to TwoInputGateBlock(gateBlockSettings, Boolean::or),
            Identifier(modID, "nor_gate")
                to TwoInputGateBlock(gateBlockSettings) { l, r -> !(l or r) },
            Identifier(modID, "long_repeater") to LongRepeaterBlock,
            Identifier(modID, "not_gate")
                to OneInputGateBlock(gateBlockSettings, Boolean::not),
            Identifier(modID, "switch")
                to TwoInTwoOutGateBlock(gateBlockSettings) { selector, data ->

                if (!selector)
                    (data to false)
                else
                    (false to data)
            },
            Identifier(modID, "intersection")
                to TwoInTwoOutGateBlock(gateBlockSettings) { a, b -> b to a },
            Identifier(modID, "half_adder")
                to TwoInTwoOutGateBlock(gateBlockSettings) { a, b ->
                val sum = a xor b
                val carry = a and b

                carry to sum
            },
            Identifier(modID, "multiplexer")
                to ThreeInputGateBlock(gateBlockSettings) { l, s, r -> if (s) l else r },
            Identifier(modID, "majority_gate")
                to ThreeInputGateBlock(gateBlockSettings) { l, b, r ->

                var c = 0

                if (l) c++
                if (b) c++
                if (r) c++

                c >= 2
            }
        )

        for ((id, block) in gateBlocks) {
            register(BLOCK, id, block)
            register(ITEM, id, BlockItem(block, gateItemSettings))

            logger.info("Registered block $id")
        }

        logger.info("Done!")
    }

}