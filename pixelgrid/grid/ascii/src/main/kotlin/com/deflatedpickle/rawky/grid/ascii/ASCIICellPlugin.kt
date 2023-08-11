/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.grid.ascii

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.grid.ascii.collection.ASCIICell
import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import com.deflatedpickle.undulation.impl.NullGlyphVector
import kotlinx.serialization.Contextual
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.event.MouseEvent.BUTTON1
import java.awt.event.MouseEvent.NOBUTTON
import java.awt.font.GlyphVector

@Plugin(
    value = "ascii_cell",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a cell that holds a character glyph
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object ASCIICellPlugin : CellProvider<GlyphVector>() {
    override val name = "ASCII"
    override var default: GlyphVector = NullGlyphVector()
    override var current: GlyphVector = NullGlyphVector()

    init {
        registry[name] = this
    }

    override fun provide(
        row: Int,
        column: Int,
    ): Cell<GlyphVector> = ASCIICell(row, column, default, PixelCellPlugin.default)

    override fun perform(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        if (cell !is ASCIICell) return

        when (button) {
            NOBUTTON, BUTTON1 -> {
                cell.content = current
                cell.colour = PixelCellPlugin.current
            }
        }
    }

    override fun redact(cell: Cell<Any>, button: Int, dragged: Boolean, clickCount: Int) {
        when (button) {
            NOBUTTON, BUTTON1 -> cell.content = default
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
        g.color = (cell as ASCIICell).colour

        if (cell.content is NullGlyphVector) return

        g.drawGlyphVector(
            cell.content,
            cell.polygon.x.toFloat(),
            cell.polygon.y.toFloat() + cell.content.logicalBounds.height.toFloat(),
        )
    }

    override fun paintHover(g: Graphics2D, cell: Cell<@Contextual Any>) {
        if (current is NullGlyphVector) return

        g.color = PixelCellPlugin.current
        // TODO: make the alpha (and rule?) customisable
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)

        g.drawGlyphVector(
            current,
            cell.polygon.x.toFloat(),
            cell.polygon.y.toFloat() + current.logicalBounds.height.toFloat(),
        )
    }
}
