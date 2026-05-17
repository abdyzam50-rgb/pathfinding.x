package com.abdy2.pathfindingx.commands

import com.abdy2.pathfindingx.PathfindingXMod
import com.abdy2.pathfindingx.api.*
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

object PathfindingCommands {
    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("pfx")
                    // /pfx goto <x> <y> <z>
                    .then(literal("goto")
                        .then(argument("x", integer())
                        .then(argument("y", integer())
                        .then(argument("z", integer()).executes { ctx ->
                            val x = getInteger(ctx, "x")
                            val y = getInteger(ctx, "y")
                            val z = getInteger(ctx, "z")
                            PathfindingXMod.process.setGoal(GoalBlock(x, y, z))
                            ctx.source.sendFeedback(Text.literal("§7Computing path to §f$x $y $z§7..."))
                            1
                        }))))
                    // /pfx near <x> <y> <z> <range>
                    .then(literal("near")
                        .then(argument("x", integer())
                        .then(argument("y", integer())
                        .then(argument("z", integer())
                        .then(argument("range", integer(1))
                        .executes { ctx ->
                            val x = getInteger(ctx, "x")
                            val y = getInteger(ctx, "y")
                            val z = getInteger(ctx, "z")
                            val r = getInteger(ctx, "range")
                            PathfindingXMod.process.setGoal(GoalNear(BlockPos(x, y, z), r))
                            ctx.source.sendFeedback(Text.literal("§7Pathing near §f$x $y $z §7(range §f$r§7)..."))
                            1
                        })))))
                    // /pfx xz <x> <z>
                    .then(literal("xz")
                        .then(argument("x", integer())
                        .then(argument("z", integer()).executes { ctx ->
                            val x = getInteger(ctx, "x")
                            val z = getInteger(ctx, "z")
                            PathfindingXMod.process.setGoal(GoalXZ(x, z))
                            ctx.source.sendFeedback(Text.literal("§7Pathing to §fX=$x Z=$z§7..."))
                            1
                        })))
                    // /pfx cancel
                    .then(literal("cancel").executes { ctx ->
                        PathfindingXMod.process.cancel()
                        ctx.source.sendFeedback(Text.literal("§cPathfinding cancelled."))
                        1
                    })
                    // /pfx status
                    .then(literal("status").executes { ctx ->
                        val p = PathfindingXMod.process
                        ctx.source.sendFeedback(
                            Text.literal("§7Status: §f${p.status} §7| Steps remaining: §f${p.currentPath.size}")
                        )
                        1
                    })
                    // /pfx config render <true|false>
                    .then(literal("config")
                        .then(literal("render")
                            .then(argument("value", word()).executes { ctx ->
                                val v = getString(ctx, "value").toBooleanStrictOrNull()
                                if (v != null) {
                                    PathfindingXMod.config.renderPath = v
                                    PathfindingXMod.config.save()
                                    ctx.source.sendFeedback(Text.literal("§aPath rendering set to §f$v"))
                                } else {
                                    ctx.source.sendFeedback(Text.literal("§cExpected true or false"))
                                }
                                1
                            }))
                    )
            )
        }
    }
}
