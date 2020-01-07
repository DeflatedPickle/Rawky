package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import com.deflatedpickle.rawky.api.component.Component
import java.awt.Graphics
import java.awt.Graphics2D

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class AnimationPreview : Component() {
    var frame = 0

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(0.5, 0.5)

        PixelGrid.drawTransparentBackground(g2D, fillType = PixelGrid.FillType.ALL, backgroundPixelDivider = 42)

        for ((layerIndex, layer) in PixelGrid.frameList[frame].layerList.withIndex().reversed()) {
            PixelGrid.drawPixels(layerIndex, layer, g2D)
        }
    }
}