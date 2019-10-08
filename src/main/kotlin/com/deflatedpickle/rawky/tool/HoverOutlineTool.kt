package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.util.Components
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics2D
import javax.swing.Icon

abstract class HoverOutlineTool(val settings: Class<*>, name: String, icon: Icon, cursor: Cursor, selected: Boolean = false) : Tool(name, icon, cursor, selected) {
    val outlineSize = 4
    val outlineStroke = BasicStroke(outlineSize.toFloat())

    override fun render(g2D: Graphics2D) {
        if (Components.pixelGrid.hoverPixel != null) {
            g2D.color = Color(Components.colourShades.selectedShade.red, Components.colourShades.selectedShade.green, Components.colourShades.selectedShade.blue, Components.pixelGrid.hoverOpacity)

            with(g2D.stroke) {
                g2D.stroke = outlineStroke
                g2D.drawRect(
                        Components.pixelGrid.hoverPixel!!.x + outlineSize / 2,
                        Components.pixelGrid.hoverPixel!!.y + outlineSize / 2,
                        Components.pixelGrid.hoverPixel!!.width * settings.getField("size").getInt(settings) - outlineSize,
                        Components.pixelGrid.hoverPixel!!.height * settings.getField("size").getInt(settings) - outlineSize
                )
                g2D.stroke = this
            }
        }
    }
}