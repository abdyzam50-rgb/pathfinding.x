package com.abdy2.pathfindingx.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File

data class PathfindingConfig(
    var renderPath: Boolean = true,
    var pathColorHex: String = "#00FF00",
    var waypointColorHex: String = "#FF8800",
    var renderDistance: Int = 128,
    var maxNodes: Int = 10_000,
    var heuristicWeight: Double = 1.5,
    var allowSprint: Boolean = true,
    var allowJump: Boolean = true,
    var allowDiagonals: Boolean = true,
    var maxFallDistance: Int = 3,
    var showHud: Boolean = true
) {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()
        private val CONFIG_FILE: File by lazy {
            FabricLoader.getInstance().configDir.resolve("pathfinding-x.json").toFile()
        }

        fun load(): PathfindingConfig {
            return if (CONFIG_FILE.exists()) {
                try {
                    GSON.fromJson(CONFIG_FILE.readText(), PathfindingConfig::class.java) ?: PathfindingConfig()
                } catch (e: Exception) {
                    PathfindingConfig()
                }
            } else {
                PathfindingConfig().also { it.save() }
            }
        }
    }

    fun save() {
        CONFIG_FILE.writeText(GSON.toJson(this))
    }

    fun pathColor(): Triple<Float, Float, Float> = hexToRgb(pathColorHex)
    fun waypointColor(): Triple<Float, Float, Float> = hexToRgb(waypointColorHex)

    private fun hexToRgb(hex: String): Triple<Float, Float, Float> {
        val clean = hex.trimStart('#')
        val color = clean.toLongOrNull(16) ?: 0x00FF00L
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        return Triple(r, g, b)
    }
}
