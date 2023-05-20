/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.shape.rectangle

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
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
    value = "rectangle",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A tool that draws lines
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ],
)
object RectanglePlugin :
    Shape(
        name = "Rectangle",
        icon = MonoIcon.SHAPE_RECT,
    ),
    Painter<Any> {
    private var firstCell: Cell<Any>? = null

    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell<Any>,
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

                    for (
                        (k, v) in process(
                            cell.column, cell.row,
                            other.column, other.column,
                        )
                    ) {
                        colourCache[k] = v

                        CellProvider.current.perform(
                            k, button, dragged, clickCount
                        )
                    }

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

    override fun paint(hoverCell: Cell<Any>, graphics: Graphics2D) {
        firstCell?.let { cell ->
            graphics.stroke = BasicStroke(4f)

            val color = CellProvider.current.current
            if (color is Color) {
                graphics.color = color
            }

            graphics.fillRect(
                (cell.row * 16) + 8, (cell.column * 16) + 8,
                (hoverCell.row * 16) + 8, (hoverCell.column * 16) + 8,
            )
        }
    }

    fun process(
        x0: Int,
        y0: Int,
        x1: Int?,
        y1: Int?,
    ): MutableMap<Cell<Any>, Color> {
        val grid: Grid
        RawkyPlugin.document!!.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            grid = layer.child
        }

        val cellMap = mutableMapOf<Cell<Any>, Color>()

        y1?.let { y1NonNull ->
            x1?.let { x1NonNull ->
                val y0Temp = min(y0, y1NonNull)
                val y1Temp = max(y0, y1NonNull)

                for (row in y0Temp..y1Temp) {
                    val x0Temp = min(x0, x1NonNull)
                    val x1Temp = max(x0, x1NonNull)

                    for (column in x0Temp..x1Temp) {
                        with(grid[row, column]) {
                            // cellMap[this] = colour
                        }
                    }
                }
            }
        }

        return cellMap
    }
}
