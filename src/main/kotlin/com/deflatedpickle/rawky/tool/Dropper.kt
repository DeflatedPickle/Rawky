/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.MouseInfo
import java.awt.Point
import javax.swing.SwingUtilities

class Dropper : HoverOutlineTool(Settings::class.java, "Dropper", listOf(Icons.colourPicker), Icons.colourPicker.image) {
    object Settings {
        @JvmField
        var size = 1
    }

    override fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        // TODO: Should colour picking push/pull to/from the undo/redo stack?
        Components.colourPicker.color = PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.table.selectedRow].pixelMatrix[PixelGrid.hoverRow][PixelGrid.hoverColumn].colour
    }

    override fun render(g2D: Graphics2D) {
        super.render(g2D)

        val mouse = MouseInfo.getPointerInfo().location.apply {
            SwingUtilities.convertPointFromScreen(this, PixelGrid)
            translate(-25, 20)
        }

        if (PixelGrid.hoverRow >= 0 && PixelGrid.hoverColumn >= 0) {
            val layerList = PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList

            for ((index, layer) in layerList.withIndex()) {
                val hoverColour = layerList[index].pixelMatrix[PixelGrid.hoverRow][PixelGrid.hoverColumn].colour

                if (hoverColour != null) {
                    g2D.color = Color.BLACK
                    g2D.stroke = BasicStroke(4f)
                    g2D.drawRect(mouse.x, mouse.y, 20, 20)
                    g2D.color = hoverColour
                    g2D.fillRect(mouse.x, mouse.y, 20, 20)

                    break
                }
            }
        }
    }
}
