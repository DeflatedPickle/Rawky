package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.*
import com.deflatedpickle.rawky.api.annotations.IntRange
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.Components
import java.awt.Graphics
import java.awt.Graphics2D

@RedrawSensitive<PixelGrid>(PixelGrid::class)
class TiledView : Component() {
    @Options
    object Settings {
        @IntRange(2, 30)
        @Tooltip("The amount of rows")
        @JvmField
        var rows = 3

        @IntRange(2, 30)
        @Tooltip("The amount of columns")
        @JvmField
        var columns = 3

        @DoubleRange(0.0, 25.0)
        @Tooltip("The padding between the tiles")
        @JvmField
        var padding = 0.0

        @DoubleRange(0.1, 4.0)
        @Tooltip("The scale of the tiles")
        @JvmField
        var scale = 0.2
    }

    init {
        isOpaque = false
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(Settings.scale, Settings.scale)

        for (row in 0 until Settings.rows) {
            for (column in 0 until Settings.columns) {
                for ((layerIndex, layer) in PixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
                    PixelGrid.drawPixels(layerIndex, layer, g2D)
                }
                g2D.translate((PixelGrid.Settings.pixelSize + Settings.padding) * PixelGrid.columnAmount, 0.0)
            }
            g2D.translate(0.0, (PixelGrid.Settings.pixelSize + Settings.padding) * PixelGrid.rowAmount)
            // Moves to the start of the row
            g2D.translate(-(PixelGrid.Settings.pixelSize + Settings.padding) * PixelGrid.columnAmount * Settings.columns, 0.0)
        }
    }
}