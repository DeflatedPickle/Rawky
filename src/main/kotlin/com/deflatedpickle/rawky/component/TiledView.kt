package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.DoubleRange
import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.EComponent
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class TiledView : JPanel() {
    @Options
    object Settings {
        @IntRange(0, 30)
        @Tooltip("The amount of rows")
        @JvmField
        var rows = 3

        @IntRange(0, 30)
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
                for ((layerIndex, layer) in Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
                    Components.pixelGrid.drawPixels(layerIndex, layer, g2D, EComponent.TILED_VIEW)
                }
                g2D.translate((Components.pixelGrid.pixelSize + Settings.padding) * Components.pixelGrid.columnAmount, 0.0)
            }
            g2D.translate(0.0, (Components.pixelGrid.pixelSize + Settings.padding) * Components.pixelGrid.rowAmount)
            // Moves to the start of the row
            g2D.translate(-(Components.pixelGrid.pixelSize + Settings.padding) * Components.pixelGrid.columnAmount * Settings.columns, 0.0)
        }
    }
}