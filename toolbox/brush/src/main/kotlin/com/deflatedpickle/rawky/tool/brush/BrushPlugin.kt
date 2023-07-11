/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.brush

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.Graphics2D
import kotlin.math.cos
import kotlin.math.sin

@Plugin(
    value = "brush",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A circle brush
    """,
    type = PluginType.OTHER,
    dependencies = ["deflatedpickle@core#1.0.0"],
    settings = BrushSettings::class,
)
object BrushPlugin :
    Tool(
        name = "Brush",
        icon = MonoIcon.BRUSH,
    ) {
    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        val action =
            object : Action(name) {
                override fun perform() {
                    ConfigUtil.getSettings<BrushSettings>("deflatedpickle@brush#")?.let {
                        // FIXME: maybe just draw the biggest circle and flood fill it
                        for (r in 0..it.size) {
                            val increment = 2 * Math.PI / 50

                            var angle = 0.0
                            while (angle < 2 * Math.PI) {
                                val x1 = (cell.row + cos(angle) * (r + 0.4)).toInt()
                                val y1 = (cell.column + sin(angle) * (r + 0.4)).toInt()

                                try {
                                    CellProvider.current.perform(cell.grid[x1, y1], button, dragged, clickCount)
                                } catch (_: IndexOutOfBoundsException) {}

                                angle += increment
                            }
                        }
                    }
                }

                override fun cleanup() {}

                override fun outline(g2D: Graphics2D) {}
            }

        ActionStack.push(action)
    }
}
