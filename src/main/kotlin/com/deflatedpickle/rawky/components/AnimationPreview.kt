package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.utils.Components
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class AnimationPreview : JPanel() {
    var frame = 0

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(0.5, 0.5)

        Components.pixelGrid.drawTransparentBackground(g2D)

        for ((layerIndex, layer) in Components.pixelGrid.frameList[frame].layerList.withIndex().reversed()) {
            Components.pixelGrid.drawPixels(layerIndex, layer, g2D)
        }
    }
}