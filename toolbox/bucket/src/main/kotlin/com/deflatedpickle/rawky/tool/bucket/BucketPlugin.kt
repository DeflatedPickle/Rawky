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
import com.deflatedpickle.owlgotrhythm.impl.fill.FourWayFloodFill
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
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
    dependencies = ["deflatedpickle@core#1.0.0"],
    settings = BucketSettings::class,
)
object BucketPlugin :
    Tool(
        name = "Bucket",
        icon = MonoIcon.PAINT_BUCKET,
    ) {
    init {
        registry["deflatedpickle@$name"] = this

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)
                ?.let { registry ->
                    registry.register(Fill::class.qualifiedName!!) { plugin, name, instance ->
                        JComboBox<Fill>().apply {
                            setRenderer { _, value, _, _, _ -> JLabel(value?.name) }

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

    override fun perform(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        // TODO: Write undo code for the fill bucket
        val action =
            object : Action(name) {
                override fun perform() {
                    action(
                        cell,
                        button,
                        dragged,
                        clickCount,
                    )
                }

                override fun cleanup() {}

                override fun outline(g2D: Graphics2D) {}
            }

        ActionStack.push(action)
    }

    private fun action(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ): MutableMap<Cell<Any>, Any> {
        val grid: Grid
        RawkyPlugin.document!!.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            grid = layer.child
        }

        return FourWayFloodFill.process(
            cell.column,
            cell.row,
        ) { cache, x, y ->
            if (x > -1 && x < grid.rows && y > -1 && y < grid.columns) {
                val c = grid[y, x]
                if (c.content == cell.content) {
                    cache[c] = c.content
                    CellProvider.current.perform(c, button, dragged, clickCount)
                    return@process true
                }
            }

            return@process false
        }
    }
}
