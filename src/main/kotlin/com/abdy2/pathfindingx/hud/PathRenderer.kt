package com.abdy2.pathfindingx.hud

import com.abdy2.pathfindingx.PathfindingXMod
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f

object PathRenderer {
    fun register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(::onWorldRender)
    }

    private fun onWorldRender(ctx: WorldRenderContext) {
        val config = PathfindingXMod.config
        if (!config.renderPath) return

        val path = PathfindingXMod.process.currentPath
        if (path.isEmpty()) return

        val camera = ctx.camera()
        val cameraPos = camera.pos
        val matrices = ctx.matrixStack() ?: MatrixStack()

        matrices.push()
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        val (r, g, b) = config.pathColor()

        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableBlend()
        RenderSystem.lineWidth(2.5f)
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)

        val tessellator = Tessellator.getInstance()
        val buf = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
        val mat: Matrix4f = matrices.peek().positionMatrix

        for (move in path) {
            val fx = move.from.x + 0.5
            val fy = move.from.y + 0.5
            val fz = move.from.z + 0.5
            val tx = move.to.x + 0.5
            val ty = move.to.y + 0.5
            val tz = move.to.z + 0.5
            buf.vertex(mat, fx.toFloat(), fy.toFloat(), fz.toFloat())
                .color(r, g, b, 0.9f)
                .normal(matrices.peek().normalMatrix, 0f, 1f, 0f)
            buf.vertex(mat, tx.toFloat(), ty.toFloat(), tz.toFloat())
                .color(r, g, b, 0.9f)
                .normal(matrices.peek().normalMatrix, 0f, 1f, 0f)
        }

        BufferRenderer.drawWithGlobalProgram(buf.endNullable() ?: run {
            matrices.pop()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            return
        })

        // Draw node boxes along the path
        val (wr, wg, wb) = config.waypointColor()
        val boxBuf = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
        for (move in path) {
            drawBoxEdges(boxBuf, mat, matrices, move.to.x.toDouble(), move.to.y.toDouble(), move.to.z.toDouble(), wr, wg, wb)
        }
        BufferRenderer.drawWithGlobalProgram(boxBuf.endNullable() ?: run {
            matrices.pop()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            return
        })

        matrices.pop()
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
    }

    private fun drawBoxEdges(
        buf: BufferBuilder, mat: Matrix4f, matrices: MatrixStack,
        x: Double, y: Double, z: Double,
        r: Float, g: Float, b: Float
    ) {
        val x0 = x.toFloat()
        val y0 = y.toFloat()
        val z0 = z.toFloat()
        val x1 = x0 + 1f
        val y1 = y0 + 1f
        val z1 = z0 + 1f
        val a = 0.6f
        val nm = matrices.peek().normalMatrix
        // Bottom square
        lineSegment(buf, mat, nm, x0, y0, z0, x1, y0, z0, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y0, z0, x1, y0, z1, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y0, z1, x0, y0, z1, r, g, b, a)
        lineSegment(buf, mat, nm, x0, y0, z1, x0, y0, z0, r, g, b, a)
        // Top square
        lineSegment(buf, mat, nm, x0, y1, z0, x1, y1, z0, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y1, z0, x1, y1, z1, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y1, z1, x0, y1, z1, r, g, b, a)
        lineSegment(buf, mat, nm, x0, y1, z1, x0, y1, z0, r, g, b, a)
        // Verticals
        lineSegment(buf, mat, nm, x0, y0, z0, x0, y1, z0, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y0, z0, x1, y1, z0, r, g, b, a)
        lineSegment(buf, mat, nm, x1, y0, z1, x1, y1, z1, r, g, b, a)
        lineSegment(buf, mat, nm, x0, y0, z1, x0, y1, z1, r, g, b, a)
    }

    private fun lineSegment(
        buf: BufferBuilder, mat: Matrix4f, nm: org.joml.Matrix3f,
        x0: Float, y0: Float, z0: Float,
        x1: Float, y1: Float, z1: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        buf.vertex(mat, x0, y0, z0).color(r, g, b, a).normal(nm, 0f, 1f, 0f)
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a).normal(nm, 0f, 1f, 0f)
    }
}
