package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.*
import javax.swing.SwingUtilities

class Dropper : HoverOutlineTool(Settings::class.java, "Dropper", Icons.colour_picker, Toolkit.getDefaultToolkit().createCustomCursor(Icons.colour_picker.image, Point(8, 16), "Colour Picker")) {
    object Settings {
        @JvmField
        var size = 1
    }

    override fun performLeft(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        // TODO: Should colour picking push/pull to/from the undo/redo stack?
        Components.colourPicker.color = Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour
    }

    override fun render(g2D: Graphics2D) {
        super.render(g2D)

        val mouse = MouseInfo.getPointerInfo().location.apply {
            SwingUtilities.convertPointFromScreen(this, Components.pixelGrid)
            translate(-25, 20)
        }

        if (Components.pixelGrid.hoverRow >= 0 && Components.pixelGrid.hoverColumn >= 0) {
            val layerList = Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList

            for ((index, layer) in layerList.withIndex()) {
                val hoverColour = layerList[index].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour

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