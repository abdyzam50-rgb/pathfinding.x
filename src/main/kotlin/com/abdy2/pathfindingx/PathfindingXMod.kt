package com.abdy2.pathfindingx

import com.abdy2.pathfindingx.commands.PathfindingCommands
import com.abdy2.pathfindingx.config.PathfindingConfig
import com.abdy2.pathfindingx.hud.PathRenderer
import com.abdy2.pathfindingx.hud.WaypointHud
import com.abdy2.pathfindingx.input.KeybindHandler
import com.abdy2.pathfindingx.process.PathfindingProcess
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object PathfindingXMod : ClientModInitializer {
    const val MOD_ID = "pathfinding-x"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    val config: PathfindingConfig by lazy { PathfindingConfig.load() }
    val process: PathfindingProcess by lazy { PathfindingProcess() }

    override fun onInitializeClient() {
        PathfindingCommands.register()
        KeybindHandler.register()
        PathRenderer.register()
        WaypointHud.register()
        LOGGER.info("PathfindingX loaded")
    }
}
