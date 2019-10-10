package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.EComponent
import com.deflatedpickle.rawky.util.Components
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
            if (layerIndex >= 0) {
                Components.pixelGrid.drawPixels(layerIndex, layer, g2D, EComponent.ANIMATION_PREVIEW)
            }
        }
    }
}