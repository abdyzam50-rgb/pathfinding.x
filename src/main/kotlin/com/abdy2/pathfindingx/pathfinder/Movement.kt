package com.abdy2.pathfindingx.pathfinder

import net.minecraft.util.math.BlockPos

enum class MoveType {
    WALK, SPRINT, JUMP, FALL, LADDER, SWIM
}

data class Move(
    val from: BlockPos,
    val to: BlockPos,
    val type: MoveType,
    val cost: Double
)

object MovementCosts {
    const val WALK = 1.0
    const val SPRINT = 0.77   // faster
    const val JUMP = 1.2
    const val FALL_PER_BLOCK = 0.5
    const val LADDER = 1.5
    const val SWIM = 2.5
    const val PENALTY_DANGER = 5.0
    const val PENALTY_SOLID_BREAK = 4.0
}
