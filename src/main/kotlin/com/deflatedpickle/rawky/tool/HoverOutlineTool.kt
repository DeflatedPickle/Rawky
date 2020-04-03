/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.Components
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import javax.swing.Icon
import javax.swing.ImageIcon

abstract class HoverOutlineTool(
    val settings: Class<*>?,
    name: String,
    iconList: List<ImageIcon>,
    selected: Toolbox.Group? = null
) : Tool(name, iconList, selected) {
    val outlineSize = 1
    val outlineStroke = BasicStroke(outlineSize.toFloat())

    override fun render(g2D: Graphics2D) {
        g2D.color = Color(
                Components.colourShades.selectedShade.red,
                Components.colourShades.selectedShade.green,
                Components.colourShades.selectedShade.blue,
                PixelGrid.Settings.hoverOpacity
        )

        with(g2D.stroke) {
            g2D.stroke = outlineStroke

            for (row in 0 until PixelGrid.previewRectangleMatrix.size) {
                for (column in 0 until PixelGrid.previewRectangleMatrix[row].size) {
                    val rectangle = PixelGrid.previewRectangleMatrix[row][column].polygon

                    if (PixelGrid.previewRectangleMatrix[row][column].colour.rgb != PixelGrid.defaultColour().rgb &&
                            PixelGrid.previewRectangleMatrix[row][column].colour.alpha != PixelGrid.defaultColour().alpha) {
                        if (PixelGrid.Shape.points == 4) {
                            if (rectangle != null) {
                                with(rectangle.bounds) {
                                    g2D.draw(this)
                                }
                            }
                        } else {
                            g2D.drawPolygon(rectangle)
                        }
                    }
                }
            }

            g2D.stroke = this
        }
    }
}
