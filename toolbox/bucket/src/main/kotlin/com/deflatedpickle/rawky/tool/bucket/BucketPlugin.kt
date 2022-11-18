/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "SpellCheckingInspection", "UNCHECKED_CAST")

package com.deflatedpickle.rawky.tool.bucket

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.extensions.get
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.Color
import java.awt.Component
import java.awt.Graphics2D
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.SwingUtilities

@Plugin(
    value = "bucket",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A bucket
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
    settings = BucketSettings::class,
)
object BucketPlugin : Tool(
    name = "Bucket",
    icon = MonoIcon.PAINT_BUCKET,
) {
    init {
        registry["deflatedpickle@$name"] = this

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)?.let { registry ->
                registry.register(Fill::class.qualifiedName!!) { plugin, name, instance ->
                    JComboBox<Fill>().apply {
                        setRenderer { _, value, _, _, _ ->
                            JLabel(value?.name)
                        }

                        SwingUtilities.invokeLater {
                            for ((_, v) in Fill.registry) {
                                addItem(v)
                            }

                            selectedItem = instance.get<Fill>(name)

                            addItemListener {
                                when (it.stateChange) {
                                    ItemEvent.SELECTED -> {
                                        instance.set(name, it.item)
                                        ConfigUtil.serializeConfig(plugin)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun perform(cell: Cell, button: Int, dragged: Boolean, clickCount: Int) {
        // TODO: Write undo code for the fill bucket
        val action = object : Action(name) {
            override fun perform() {
                process(cell.row, cell.column, cell.colour)
            }

            override fun cleanup() {
            }

            override fun outline(g2D: Graphics2D) {
            }
        }

        ActionStack.push(action)
    }

    fun process(row: Int, column: Int, colour: Color) {
        val grid: Grid
        RawkyPlugin.document!!.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            grid = layer.child
        }

        // Kotlin's ArrayDeque doesn't have a poll equivalent as far as I know
        val cellList = java.util.ArrayDeque<Pair<Int, Int>>()

        cellList.add(Pair(row, column))

        // http://steve.hollasch.net/cgindex/polygons/floodfill.html
        while (cellList.isNotEmpty()) {
            with(cellList.poll()) {
                if (this.first in 0 until grid.rows &&
                    this.second in 0 until grid.columns
                ) {
                    val cell = grid[this.first, this.second]

                    val cellColour = cell.colour
                    // val rgb = cellColour.rgb
                    // val hsb = Color.RGBtoHSB(cellColour.red, cellColour.green, cellColour.blue, null)

                    if (cellColour == colour) {
                        ConfigUtil.getSettings<BucketSettings>("deflatedpickle@bucket#*")?.let {
                            it.fill?.perform(cell, this.first, this.second, RawkyPlugin.colour)
                        }

                        cellList.add(Pair(this.first, this.second + 1))
                        cellList.add(Pair(this.first, this.second - 1))
                        cellList.add(Pair(this.first + 1, this.second))
                        cellList.add(Pair(this.first - 1, this.second))
                    }
                }
            }
        }
    }
}
