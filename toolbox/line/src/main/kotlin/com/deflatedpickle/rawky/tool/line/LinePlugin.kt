/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.line

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.owlgotrhythm.impl.line.BresenhamLine
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

@Plugin(
    value = "line",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A tool that draws lines
    """,
    type = PluginType.OTHER,
    dependencies = ["deflatedpickle@core#1.0.0"],
    settings = LineSettings::class,
)
object LinePlugin :
    Tool(
        name = "Line",
        icon = MonoIcon.LINE,
    ),
    Painter<Any> {
    private var firstCell: Cell<Any>? = null

    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        val other = firstCell
        // First point
        if (other == null) {
            firstCell = cell
        }
        // Second point
        else {
            val action =
                object : Action(name) {
                    val cache = mutableMapOf<Cell<Any>, Any>()

                    override fun perform() {
                        cache.clear()
                        cache.putAll(
                            action(
                                cell,
                                other,
                                button,
                                dragged,
                                clickCount,
                            ),
                        )
                    }

                    override fun cleanup() {
                        for ((c, content) in cache) {
                            CellProvider.current.cleanup(content, c, button, dragged, clickCount)
                        }
                        cache.clear()
                    }

                    override fun outline(g2D: Graphics2D) {}
                }

            ActionStack.push(action)

            ConfigUtil.getSettings<LineSettings>("deflatedpickle@line#*")?.let {
                firstCell =
                    when (it.mode) {
                        SINGLE -> null
                        CONTINUOUS -> cell
                    }
            }
        }

        if (clickCount == 2) {
            firstCell = null
        }
    }

    override fun paint(hoverCell: Cell<Any>, graphics: Graphics2D) {
        firstCell?.let { cell ->
            graphics.stroke = BasicStroke(4f)

            val color = CellProvider.current.current
            if (color is Color) {
                graphics.color = color
            }

            graphics.drawLine(
                (cell.row * 16) + 8,
                (cell.column * 16) + 8,
                (hoverCell.row * 16) + 8,
                (hoverCell.column * 16) + 8,
            )
        }
    }

    private fun action(
        cell: Cell<Any>,
        other: Cell<Any>,
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

        return BresenhamLine.process(
            cell.column,
            cell.row,
            other.column,
            other.row,
        ) { cache, x, y ->
            grid[y, x].apply {
                cache[this] = this.content
                CellProvider.current.perform(this, button, dragged, clickCount)
            }
        }
    }
}
