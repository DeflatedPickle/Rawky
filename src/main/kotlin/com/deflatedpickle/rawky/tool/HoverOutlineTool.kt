/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.util.Components
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import javax.swing.Icon

abstract class HoverOutlineTool(val settings: Class<*>, name: String, iconList: List<Icon>, cursor: Image, selected: Boolean = false) : Tool(name, iconList, cursor, selected) {
    val outlineSize = 4
    val outlineStroke = BasicStroke(outlineSize.toFloat())

    override fun render(g2D: Graphics2D) {
        PixelGrid.hoverPixel?.let {
            g2D.color = Color(
                    Components.colourShades.selectedShade.red,
                    Components.colourShades.selectedShade.green,
                    Components.colourShades.selectedShade.blue,
                    PixelGrid.Settings.hoverOpacity
            )

            with(g2D.stroke) {
                g2D.stroke = outlineStroke

                if (PixelGrid.Shape.points == 4) {
                    with(it.bounds) {
                        this.grow(3, 3)
                        g2D.drawRect(this.x, this.y, this.width, this.height)
                    }
                } else {
                    g2D.drawPolygon(it)
                }

                g2D.stroke = this
            }
        }
    }
}
