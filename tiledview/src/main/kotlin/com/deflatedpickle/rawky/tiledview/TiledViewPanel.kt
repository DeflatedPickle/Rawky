/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tiledview

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object TiledViewPanel : PluginPanel() {
    val tilePanel = object : JXPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            var rows = 3
            var columns = 3
            var xpad = 0.0
            var ypad = 0.0
            var scale = 0.2

            ConfigUtil.getSettings<TiledViewSettings>("deflatedpickle@tiled_view#*")?.let {
                rows = it.rows
                columns = it.columns
                xpad = it.xPadding
                ypad = it.yPadding
                scale = it.scale
            }

            val g2d = g as Graphics2D
            val bufferedImage = BufferedImage(
                visibleRect.x + visibleRect.width,
                visibleRect.y + visibleRect.height,
                BufferedImage.TYPE_INT_ARGB
            )

            g2d.scale(scale, scale)

            RawkyPlugin.document?.let { doc ->
                for (
                    v in PaintLayer.registry.getAll().values
                        .filter { it.layer == LayerCategory.GRID }
                ) {
                    for (row in 0 until rows) {
                        for (column in 0 until columns) {
                            val temp = bufferedImage.createGraphics()

                            doc.children[doc.selectedIndex].let { frame ->
                                for (layer in frame.children) {
                                    v.paint(doc, frame, layer, temp)
                                    temp.dispose()
                                }
                            }

                            g2d.drawRenderedImage(bufferedImage, null)

                            g2d.translate((Grid.pixel + xpad) * doc.rows, 0.0)
                        }

                        g2d.translate(0.0, (Grid.pixel + ypad) * doc.columns)

                        // Move back to start
                        g2d.translate(((Grid.pixel + xpad) * doc.columns * columns) * -1.0, 0.0)
                    }
                }
            }
        }
    }

    init {
        layout = BorderLayout()

        add(tilePanel, BorderLayout.CENTER)
    }
}
