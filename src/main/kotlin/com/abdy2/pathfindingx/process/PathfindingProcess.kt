package com.abdy2.pathfindingx.process

import com.abdy2.pathfindingx.PathfindingXMod
import com.abdy2.pathfindingx.api.Goal
import com.abdy2.pathfindingx.pathfinder.AStarPathfinder
import com.abdy2.pathfindingx.pathfinder.Move
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class PathfindingProcess {
    @Volatile var currentGoal: Goal? = null
        private set
    @Volatile var currentPath: List<Move> = emptyList()
        private set
    @Volatile var status: Status = Status.IDLE
        private set

    private val running = AtomicBoolean(false)
    private var future: CompletableFuture<Void>? = null

    enum class Status { IDLE, COMPUTING, EXECUTING, FAILED, ARRIVED }

    fun setGoal(goal: Goal) {
        cancel()
        currentGoal = goal
        status = Status.COMPUTING
        running.set(true)

        val client = MinecraftClient.getInstance()
        val world = client.world ?: run { status = Status.FAILED; return }
        val startPos = client.player?.blockPos ?: run { status = Status.FAILED; return }

        val cfg = PathfindingXMod.config
        val pathfinder = AStarPathfinder(cfg.maxNodes, cfg.heuristicWeight)

        future = CompletableFuture.runAsync {
            val path = pathfinder.findPath(startPos, goal, world)
            client.execute {
                if (running.get()) {
                    currentPath = path
                    status = if (path.isEmpty()) Status.FAILED else Status.EXECUTING
                    val msg = if (path.isEmpty())
                        Text.literal("§cNo path found.")
                    else
                        Text.literal("§aPath found — §f${path.size} §asteps")
                    client.player?.sendMessage(msg, false)
                }
            }
        }
    }

    fun cancel() {
        running.set(false)
        future?.cancel(true)
        future = null
        currentGoal = null
        currentPath = emptyList()
        status = Status.IDLE
    }

    fun recompute() {
        val goal = currentGoal ?: return
        setGoal(goal)
    }

    /** Call from nearest waypoint in path that player has reached. */
    fun advancePath(currentPos: BlockPos) {
        val path = currentPath
        if (path.isEmpty()) return
        val idx = path.indexOfFirst { it.to == currentPos }
        if (idx >= 0) {
            currentPath = path.drop(idx + 1)
            if (currentPath.isEmpty()) {
                status = Status.ARRIVED
                MinecraftClient.getInstance().player?.sendMessage(
                    Text.literal("§aGoal reached!"), false
                )
            }
        }
    }
}
