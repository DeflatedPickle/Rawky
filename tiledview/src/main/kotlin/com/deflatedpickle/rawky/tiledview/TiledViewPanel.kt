/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tiledview

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.util.DrawUtil
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object TiledViewPanel : PluginPanel() {
    val tilePanel =
        object : JXPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)

                var rows = 3
                var columns = 3
                var xpad = 0.0
                var ypad = 0.0

                ConfigUtil.getSettings<TiledViewSettings>("deflatedpickle@tiled_view#*")?.let {
                    rows = it.rows
                    columns = it.columns
                    xpad = it.xPadding
                    ypad = it.yPadding
                }

                val g2D = g as Graphics2D

                RawkyPlugin.document?.let { doc ->
                    val factor = DrawUtil.getScaleFactor(
                        width.toDouble() / Grid.pixel, height.toDouble() / Grid.pixel,
                        doc.columns.toDouble() * columns, doc.rows.toDouble() * columns
                    )
                    g2D.scale(factor, factor)

                    for (
                    v in
                    PaintLayer.registry.getAll().values.filter { it.layer == LayerCategory.GRID }
                    ) {
                        for (row in 0 until rows) {
                            for (column in 0 until columns) {
                                doc.children[doc.selectedIndex].let { frame ->
                                    for (layer in frame.children.indices) {
                                        v.paint(doc, doc.selectedIndex, layer, g2D)
                                    }
                                }

                                g2D.translate((Grid.pixel + xpad) * doc.rows, 0.0)
                            }

                            g2D.translate(0.0, (Grid.pixel + ypad) * doc.columns)

                            // Move back to start
                            g2D.translate(((Grid.pixel + xpad) * doc.columns * columns) * -1.0, 0.0)
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
