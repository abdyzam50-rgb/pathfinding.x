package com.abdy2.pathfindingx.input

import com.abdy2.pathfindingx.PathfindingXMod
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeybindHandler {
    private const val CATEGORY = "key.category.pathfinding-x"

    val KEY_CANCEL = KeyBindingHelper.registerKeyBinding(
        KeyBinding("key.pathfinding-x.cancel", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, CATEGORY)
    )
    val KEY_TOGGLE_RENDER = KeyBindingHelper.registerKeyBinding(
        KeyBinding("key.pathfinding-x.toggle_render", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, CATEGORY)
    )
    val KEY_RECOMPUTE = KeyBindingHelper.registerKeyBinding(
        KeyBinding("key.pathfinding-x.recompute", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, CATEGORY)
    )
    val KEY_TOGGLE_HUD = KeyBindingHelper.registerKeyBinding(
        KeyBinding("key.pathfinding-x.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F7, CATEGORY)
    )

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            while (KEY_CANCEL.wasPressed()) {
                PathfindingXMod.process.cancel()
                PathfindingXMod.LOGGER.info("Pathfinding cancelled via keybind")
            }
            while (KEY_TOGGLE_RENDER.wasPressed()) {
                PathfindingXMod.config.renderPath = !PathfindingXMod.config.renderPath
                PathfindingXMod.config.save()
            }
            while (KEY_RECOMPUTE.wasPressed()) {
                PathfindingXMod.process.recompute()
            }
            while (KEY_TOGGLE_HUD.wasPressed()) {
                PathfindingXMod.config.showHud = !PathfindingXMod.config.showHud
                PathfindingXMod.config.save()
            }
        }
    }
}
