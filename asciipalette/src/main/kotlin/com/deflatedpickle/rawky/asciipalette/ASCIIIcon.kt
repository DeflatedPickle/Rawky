package com.deflatedpickle.rawky.asciipalette

import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.font.GlyphVector
import javax.swing.Icon

class ASCIIIcon(
    val glyph: GlyphVector
) : Icon {
    private var width = -1
    private var height = -1

    init {
        width = glyph.logicalBounds.width.toInt()
        height = glyph.logicalBounds.height.toInt()
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        if (g is Graphics2D) {
            g.color = PixelCellPlugin.current
            g.drawGlyphVector(
                glyph,
                x.toFloat(),
                y.toFloat() + height
            )
        }
    }

    override fun getIconWidth() = width
    override fun getIconHeight() = height
}