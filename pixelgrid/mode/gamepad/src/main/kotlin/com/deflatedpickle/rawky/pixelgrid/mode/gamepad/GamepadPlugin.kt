/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.mode.gamepad

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.api.ControlMode
import net.java.games.input.Component.Identifier.Axis
import net.java.games.input.Component.Identifier.Button
import net.java.games.input.Component.POV
import java.awt.Point
import java.awt.event.MouseEvent

@Plugin(
    value = "gamepad",
    author = "DeflatedPickle",
    version = "1.1.0",
    description = """
        <br>
        Use a gamepad to paint
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object GamepadPlugin : ControlMode() {
    override val name = "Gamepad"
    val point = Point(0, 0)

    override fun apply() {
        GamePadObserver.watch()
    }

    override fun remove() {
        GamePadObserver.blind()
    }

    init {
        registry[name] = this

        GamePadObserver.addListener() {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]
                val layer = frame.children[frame.selectedIndex]
                val grid = layer.child

                when (it.identifier) {
                    Button.A -> {}
                    Button.B -> {}
                    Button.X -> {}
                    Button.Y -> {}
                    Button.SELECT -> {}
                    Button.START -> {}
                    Button.MODE -> {
                        // TODO: make a command palette to bring up
                    }
                    Button.LEFT_THUMB -> {
                        if (Tool.registry.values.indexOf(Tool.current) - 1 >= 0) {
                            Tool.current = Tool.registry.values.toList()[Tool.registry.values.indexOf(Tool.current) - 1]
                        } else {
                            Tool.current = Tool.registry.values.last()
                        }

                        EventChangeTool.trigger(Tool.current)
                    }

                    Button.RIGHT_THUMB -> {
                        if (Tool.registry.values.indexOf(Tool.current) + 1 < Tool.registry.values.size) {
                            Tool.current =
                                Tool.registry.values.toList()[Tool.registry.values.indexOf(Tool.current) + 1]
                        } else {
                            Tool.current = Tool.registry.values.first()
                        }

                        EventChangeTool.trigger(Tool.current)
                    }

                    Button.LEFT_THUMB2 -> {}
                    Button.RIGHT_THUMB2 -> {
                        PixelGridPanel.paint(
                            MouseEvent.BUTTON1,
                            false,
                            1,
                        )
                    }
                    Button.LEFT_THUMB3 -> {}
                    Button.RIGHT_THUMB3 -> {}
                    Axis.POV -> {
                        when (it.data) {
                            POV.UP -> {
                                if (point.y - 1 >= 0) {
                                    point.setLocation(point.x, point.y - 1)
                                } else {
                                    point.setLocation(point.x, grid.rows)
                                }
                            }

                            POV.LEFT -> {
                                if (point.x - 1 >= 0) {
                                    point.setLocation(point.x - 1, point.y)
                                } else {
                                    point.setLocation(grid.columns, point.y)
                                }
                            }
                            POV.DOWN -> {
                                if (point.y + 1 < grid.rows) {
                                    point.setLocation(point.x, point.y + 1)
                                } else {
                                    point.setLocation(point.x, 0)
                                }
                            }

                            POV.RIGHT -> {
                                if (point.x + 1 < grid.columns) {
                                    point.setLocation(point.x + 1, point.y)
                                } else {
                                    point.setLocation(0, point.y)
                                }
                            }
                        }
                    }
                    Axis.X -> {}
                    Axis.Y -> {}
                    Axis.Z -> {}
                    else -> return@addListener
                }

                PixelGridPanel.selectedCells.clear()
                PixelGridPanel.selectedCells.add(grid[point.x, point.y])
                EventUpdateGrid.trigger(grid)
            }
        }
    }
}
