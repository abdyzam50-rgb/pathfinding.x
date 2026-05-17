package com.abdy2.pathfindingx.pathfinder

import com.abdy2.pathfindingx.api.Goal
import net.minecraft.block.FluidBlock
import net.minecraft.block.LadderBlock
import net.minecraft.block.SlabBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import java.util.PriorityQueue

class AStarPathfinder(
    private val maxNodes: Int = 10_000,
    private val heuristicWeight: Double = 1.5
) {
    private data class Node(
        val pos: BlockPos,
        val g: Double,
        val h: Double,
        val parent: Node?,
        val move: Move?
    ) : Comparable<Node> {
        val f get() = g + h
        override fun compareTo(other: Node) = f.compareTo(other.f)
    }

    fun findPath(start: BlockPos, goal: Goal, world: BlockView): List<Move> {
        val open = PriorityQueue<Node>()
        val bestG = HashMap<Long, Double>()

        fun key(pos: BlockPos): Long = pos.asLong()

        open.add(Node(start, 0.0, goal.heuristic(start.x, start.y, start.z) * heuristicWeight, null, null))
        bestG[key(start)] = 0.0

        var explored = 0
        while (open.isNotEmpty() && explored < maxNodes) {
            val current = open.poll()
            explored++

            if (goal.isInGoal(current.pos.x, current.pos.y, current.pos.z)) {
                return reconstructPath(current)
            }

            for (move in generateMoves(current.pos, world)) {
                val newG = current.g + move.cost
                val k = key(move.to)
                if (newG < (bestG[k] ?: Double.MAX_VALUE)) {
                    bestG[k] = newG
                    val h = goal.heuristic(move.to.x, move.to.y, move.to.z) * heuristicWeight
                    open.add(Node(move.to, newG, h, current, move))
                }
            }
        }

        return emptyList()
    }

    private fun reconstructPath(end: Node): List<Move> {
        val path = mutableListOf<Move>()
        var node: Node? = end
        while (node?.move != null) {
            path.add(node.move!!)
            node = node.parent
        }
        path.reverse()
        return path
    }

    private fun generateMoves(pos: BlockPos, world: BlockView): List<Move> {
        val moves = mutableListOf<Move>()
        val cardinals = listOf(
            BlockPos(1, 0, 0), BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1), BlockPos(0, 0, -1)
        )
        val diagonals = listOf(
            BlockPos(1, 0, 1), BlockPos(-1, 0, 1),
            BlockPos(1, 0, -1), BlockPos(-1, 0, -1)
        )

        for (dir in cardinals) {
            val dest = pos.add(dir)
            // Walk same level
            if (canStandAt(dest, world) && isPassable(dest, world) && isPassable(dest.up(), world)) {
                val type = MoveType.SPRINT
                moves.add(Move(pos, dest, type, MovementCosts.SPRINT))
            }
            // Jump up one
            val destUp = dest.up()
            if (canStandAt(destUp, world) && isPassable(destUp, world) && isPassable(destUp.up(), world) && isPassable(pos.up(2), world)) {
                moves.add(Move(pos, destUp, MoveType.JUMP, MovementCosts.JUMP))
            }
            // Fall down up to 3 blocks
            for (drop in 1..3) {
                val destDown = dest.down(drop)
                if (canStandAt(destDown, world)) {
                    moves.add(Move(pos, destDown, MoveType.FALL, MovementCosts.WALK + drop * MovementCosts.FALL_PER_BLOCK))
                    break
                }
                if (!isAir(dest.down(drop), world)) break
            }
        }
        // Diagonal walk
        for (dir in diagonals) {
            val dest = pos.add(dir)
            if (canStandAt(dest, world) && isPassable(dest, world) && isPassable(dest.up(), world)) {
                moves.add(Move(pos, dest, MoveType.SPRINT, MovementCosts.SPRINT * 1.414))
            }
        }
        // Ladder
        if (world.getBlockState(pos).block is LadderBlock) {
            moves.add(Move(pos, pos.up(), MoveType.LADDER, MovementCosts.LADDER))
        }
        return moves
    }

    private fun canStandAt(pos: BlockPos, world: BlockView): Boolean {
        val below = pos.down()
        val belowState = world.getBlockState(below)
        return !belowState.isAir && belowState.isSolidBlock(world, below) &&
               isAir(pos, world) && isAir(pos.up(), world)
    }

    private fun isPassable(pos: BlockPos, world: BlockView): Boolean {
        val state = world.getBlockState(pos)
        return state.isAir || state.block is FluidBlock || state.block is SlabBlock
    }

    private fun isAir(pos: BlockPos, world: BlockView): Boolean =
        world.getBlockState(pos).isAir
}
