/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.line

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.Painter
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.tool.line.api.Mode.CONTINUOUS
import com.deflatedpickle.rawky.tool.line.api.Mode.SINGLE
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.abs

@Plugin(
    value = "line",
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
    settings = LineSettings::class,
)
object LinePlugin :
    Tool(
        name = "Line",
        icon = MonoIcon.LINE,
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
        clickCount: Int
    ) {
        val other = firstCell
        // First point
        if (other == null) {
            firstCell = cell
        }
        // Second point
        else {
            val action = object : Action(name) {
                val colourCache = mutableMapOf<Cell<out Any>, Color>()

                override fun perform() {
                    colourCache.clear()
                    colourCache.putAll(
                        process(
                            cell.column, cell.row,
                            other.column, other.column,
                        )
                    )
                }

                override fun cleanup() {
                    for ((c, colour) in colourCache) {
                        CellProvider.current.perform(
                            c, button, dragged, clickCount
                        )
                    }
                }

                override fun outline(g2D: Graphics2D) {
                }
            }

            ActionStack.push(action)

            ConfigUtil.getSettings<LineSettings>("deflatedpickle@line#*")?.let {
                firstCell = when (it.mode) {
                    SINGLE -> null
                    CONTINUOUS -> cell
                }
            }
        }

        if (clickCount == 2) {
            firstCell = null
        }
    }

    override fun paint(
        hoverCell: Cell<out Any>,
        graphics: Graphics2D
    ) {
        firstCell?.let { cell ->
            graphics.stroke = BasicStroke(4f)

            val color = CellProvider.current.current
            if (color is Color) {
                graphics.color = color
            }

            graphics.drawLine(
                (cell.row * 16) + 8, (cell.column * 16) + 8,
                (hoverCell.row * 16) + 8, (hoverCell.column * 16) + 8,
            )
        }
    }

    private fun <T> process(
        x0: Int,
        y0: Int,
        x1: Int?,
        y1: Int?,
    ): MutableMap<Cell<out Any>, T> {
        val grid: Grid
        RawkyPlugin.document!!.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            grid = layer.child
        }

        // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#External_links
        var tempX0 = x0
        var tempY0 = y0

        val dx = abs(x1!! - tempX0)
        val sx = if (tempX0 < x1) 1 else -1
        val dy = -abs(y1!! - tempY0)
        val sy = if (tempY0 < y1) 1 else -1
        var err = dx + dy

        val cellMap = mutableMapOf<Cell<out Any>, Color>()

        while (true) {
            with(grid[tempY0, tempX0]) {
                // TODO
                /*cellMap[this] = colour
                colour = RawkyPlugin.colour*/
            }

            if (tempX0 == x1 && tempY0 == y1) break

            val e2 = 2 * err

            if (e2 >= dy) {
                err += dy
                tempX0 += sx
            }

            if (e2 <= dx) {
                err += dx
                tempY0 += sy
            }
        }

        return cellMap as MutableMap<Cell<out Any>, T>
    }
}
