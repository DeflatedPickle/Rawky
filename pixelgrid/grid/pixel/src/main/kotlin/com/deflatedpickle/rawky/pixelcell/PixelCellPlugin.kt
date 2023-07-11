/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelcell

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.pixelcell.collection.PixelCell
import kotlinx.serialization.Contextual
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

@Plugin(
    value = "pixel_cell",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a cell that holds a colour
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object PixelCellPlugin : CellProvider<Color>() {
    override val name = "Pixel"
    override var default: Color = Color(0, 0, 0, 0)
    override var current: Color = default

    init {
        registry[name] = this

        current = Color.BLACK
    }

    override fun provide(
        row: Int,
        column: Int,
    ): PixelCell = PixelCell(row, column, PixelCell.default)

    override fun perform(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        when (button) {
            0 -> cell.content = current
        }
    }

    override fun redact(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        when (button) {
            0 -> cell.content = PixelCell.default
        }
    }

    override fun cleanup(
        cache: Any,
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        cell.content = cache
    }

    override fun paintGrid(g: Graphics2D, cell: Cell<@Contextual Any>) {
        g.color = cell.content as Color
        g.fillRect(cell.polygon.x, cell.polygon.y, cell.polygon.width, cell.polygon.height)
    }

    override fun paintHover(g: Graphics2D, cell: Cell<@Contextual Any>) {
        g.stroke = BasicStroke(4f)
        g.color = Color.BLACK
        g.drawRect(cell.polygon.x, cell.polygon.y, cell.polygon.width, cell.polygon.height)
    }
}
