package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Components
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class TiledView : JPanel() {
    var rows = 3
    var columns = 3

    var scale = 0.2
    var padding = 0

    init {
        isOpaque = false
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(scale, scale)

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                for ((layerIndex, layer) in Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
                    Components.pixelGrid.drawPixels(layerIndex, layer, g2D)
                }
                g2D.translate((Components.pixelGrid.pixelSize + padding) * 16, 0)
            }
            g2D.translate(0, (Components.pixelGrid.pixelSize + padding) * 16)
            g2D.translate(-(Components.pixelGrid.pixelSize + padding) * 16 * columns, 0)
        }
    }
}