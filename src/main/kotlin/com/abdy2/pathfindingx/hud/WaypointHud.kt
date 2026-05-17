package com.abdy2.pathfindingx.hud

import com.abdy2.pathfindingx.PathfindingXMod
import com.abdy2.pathfindingx.process.PathfindingProcess
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

object WaypointHud {
    fun register() {
        HudRenderCallback.EVENT.register(::onHudRender)
    }

    private fun onHudRender(drawContext: DrawContext, tickCounter: RenderTickCounter) {
        val config = PathfindingXMod.config
        if (!config.showHud) return

        val process = PathfindingXMod.process
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val renderer = client.textRenderer

        val status = process.status
        val path = process.currentPath
        val goal = process.currentGoal

        // Don't render when idle with no goal
        if (status == PathfindingProcess.Status.IDLE) return

        val lines = mutableListOf<String>()

        val statusColor = when (status) {
            PathfindingProcess.Status.COMPUTING  -> "§e"
            PathfindingProcess.Status.EXECUTING  -> "§a"
            PathfindingProcess.Status.ARRIVED    -> "§b"
            PathfindingProcess.Status.FAILED     -> "§c"
            PathfindingProcess.Status.IDLE       -> "§7"
        }

        lines.add("§7[PathfindingX] ${statusColor}${status.name}")

        if (path.isNotEmpty()) {
            lines.add("§7Steps remaining: §f${path.size}")
            val next = path.first().to
            val dx = next.x - player.blockX
            val dy = next.y - player.blockY
            val dz = next.z - player.blockZ
            val dist = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())
            lines.add("§7Next: §f${next.x} ${next.y} ${next.z} §7(§f%.1f§7m)".format(dist))
        }

        if (goal != null) {
            lines.add("§7Goal: §f${goal::class.simpleName}")
        }

        val x = 4
        var y = 4
        val bgColor = 0x88000000.toInt()
        val lineH = renderer.fontHeight + 2

        for (line in lines) {
            drawContext.fill(x - 2, y - 1, x + renderer.getWidth(line) + 2, y + lineH - 1, bgColor)
            drawContext.drawText(renderer, line, x, y, 0xFFFFFF, false)
            y += lineH
        }
    }
}
