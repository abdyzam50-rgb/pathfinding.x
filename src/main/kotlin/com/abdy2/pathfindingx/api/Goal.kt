package com.abdy2.pathfindingx.api

import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.sqrt

interface Goal {
    fun isInGoal(x: Int, y: Int, z: Int): Boolean
    fun heuristic(x: Int, y: Int, z: Int): Double
}

data class GoalBlock(val pos: BlockPos) : Goal {
    constructor(x: Int, y: Int, z: Int) : this(BlockPos(x, y, z))

    override fun isInGoal(x: Int, y: Int, z: Int) =
        x == pos.x && y == pos.y && z == pos.z

    override fun heuristic(x: Int, y: Int, z: Int): Double {
        val dx = (x - pos.x).toDouble()
        val dy = (y - pos.y).toDouble()
        val dz = (z - pos.z).toDouble()
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}

data class GoalXZ(val targetX: Int, val targetZ: Int) : Goal {
    override fun isInGoal(x: Int, y: Int, z: Int) = x == targetX && z == targetZ

    override fun heuristic(x: Int, y: Int, z: Int): Double {
        val dx = (x - targetX).toDouble()
        val dz = (z - targetZ).toDouble()
        return sqrt(dx * dx + dz * dz)
    }
}

data class GoalNear(val pos: BlockPos, val range: Int) : Goal {
    override fun isInGoal(x: Int, y: Int, z: Int): Boolean {
        val dx = x - pos.x
        val dy = y - pos.y
        val dz = z - pos.z
        return dx * dx + dy * dy + dz * dz <= range * range
    }

    override fun heuristic(x: Int, y: Int, z: Int): Double {
        val dx = (x - pos.x).toDouble()
        val dy = (y - pos.y).toDouble()
        val dz = (z - pos.z).toDouble()
        return max(0.0, sqrt(dx * dx + dy * dy + dz * dz) - range)
    }
}

data class GoalRunAway(val from: BlockPos, val minDistance: Double) : Goal {
    override fun isInGoal(x: Int, y: Int, z: Int): Boolean {
        val dx = (x - from.x).toDouble()
        val dy = (y - from.y).toDouble()
        val dz = (z - from.z).toDouble()
        return sqrt(dx * dx + dy * dy + dz * dz) >= minDistance
    }

    override fun heuristic(x: Int, y: Int, z: Int): Double {
        val dx = (x - from.x).toDouble()
        val dy = (y - from.y).toDouble()
        val dz = (z - from.z).toDouble()
        return max(0.0, minDistance - sqrt(dx * dx + dy * dy + dz * dz))
    }
}

data class GoalComposite(val goals: List<Goal>) : Goal {
    override fun isInGoal(x: Int, y: Int, z: Int) = goals.any { it.isInGoal(x, y, z) }
    override fun heuristic(x: Int, y: Int, z: Int) = goals.minOf { it.heuristic(x, y, z) }
}
