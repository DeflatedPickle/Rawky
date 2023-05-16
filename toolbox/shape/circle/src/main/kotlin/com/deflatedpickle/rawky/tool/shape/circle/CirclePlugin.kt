/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.shape.circle

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Painter
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.tool.shape.api.Shape
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.max
import kotlin.math.min

@Plugin(
    value = "circle",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A tool that draws circle
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
)
object CirclePlugin :
    Shape(
        name = "Circle",
        icon = MonoIcon.SHAPE_OVAL,
    ),
    Painter<Any> {
    private var firstCell: Cell<out Any>? = null

    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell<out Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        if (dragged) return

        val other = firstCell
        // First point
        if (other == null) {
            firstCell = cell
        }
        // Second point
        else {
            val action = object : Action(name) {
                val colourCache = mutableMapOf<Cell<Any>, Color>()

                override fun perform() {
                    colourCache.clear()

                    colourCache.putAll(
                        process(
                            cell.column, cell.row,
                            other.column,
                        )
                    )

                    firstCell = null
                }

                override fun cleanup() {
                    for ((c, colour) in colourCache) {
                        // c.colour = colour
                    }
                }

                override fun outline(g2D: Graphics2D) {
                }
            }

            ActionStack.push(action)
        }
    }

    override fun paint(hoverCell: Cell<out Any>, graphics: Graphics2D) {
        firstCell?.let { cell ->
            graphics.stroke = BasicStroke(4f)
            graphics.color = RawkyPlugin.colour
            graphics.fillOval(
                (cell.row * 16) + 8, (cell.column * 16) + 8,
                (hoverCell.row * 16) + 8, (hoverCell.column * 16) + 8,
            )
        }
    }

    private fun <T> process(
        x0: Int,
        y0: Int,
        x1: Int,
    ): MutableMap<Cell<Any>, T> {
        val grid: Grid
        RawkyPlugin.document!!.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            grid = layer.child
        }

        val cellMap = mutableMapOf<Cell<Any>, Color>()

        // https://www.geeksforgeeks.org/mid-point-circle-drawing-algorithm/

        var r = max(x0, x1) - min(x0, x1)

        while (r > 0) {
            var x: Int = r
            var y = 0

            // TODO
            // grid[x + x0, y + y0].colour = RawkyPlugin.colour

            if (r > 0) {
                /*grid[x + x0, -y + y0].colour = RawkyPlugin.colour
                grid[y + x0, x + y0].colour = RawkyPlugin.colour
                grid[-y + x0, x + y0].colour = RawkyPlugin.colour*/
            }

            var p: Int = 1 - r
            while (x > y) {
                y++

                if (p <= 0) p += (2 * y) + 1 else {
                    x--
                    p = p + 2 * y - 2 * x + 1
                }

                if (x < y) break

                /*grid[x + x0, y + y0].colour = RawkyPlugin.colour
                grid[-x + x0, y + y0].colour = RawkyPlugin.colour
                grid[x + x0, -y + y0].colour = RawkyPlugin.colour
                grid[-x + x0, -y + y0].colour = RawkyPlugin.colour*/

                if (x != y) {
                    /*grid[y + x0, x + y0].colour = RawkyPlugin.colour
                    grid[-y + x0, x + y0].colour = RawkyPlugin.colour
                    grid[y + x0, -x + y0].colour = RawkyPlugin.colour
                    grid[-y + x0, -x + y0].colour = RawkyPlugin.colour*/
                }
            }

            r -= 1
        }

        return cellMap as MutableMap<Cell<Any>, T>
    }
}
