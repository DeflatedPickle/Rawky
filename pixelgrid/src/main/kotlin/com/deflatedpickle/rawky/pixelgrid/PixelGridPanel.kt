/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JToolBar

// TODO: add d&d of images
@RedrawActive
object PixelGridPanel : PluginPanel() {
    val selectedCells = mutableListOf<Cell<Any>>()

    private val panel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            RawkyPlugin.document?.let { doc ->
                doc.children.getOrNull(doc.selectedIndex)?.let { frame ->
                    val g2d = g as Graphics2D
                    val bufferedImage =
                        BufferedImage(
                            visibleRect.x + visibleRect.width,
                            visibleRect.y + visibleRect.height,
                            BufferedImage.TYPE_INT_ARGB,
                        )

                    for (v in PaintLayer.registry.getAll().values.sortedBy { it.layer }) {
                        val temp = bufferedImage.createGraphics()

                        for ((i, layer) in frame.children.withIndex()) {
                            if (layer.visible) {
                                v.paint(doc, doc.selectedIndex, i, temp)
                            }
                        }

                        temp.dispose()
                    }

                    g2d.drawRenderedImage(bufferedImage, null)
                }
            }
        }
    }

    init {
        layout = BorderLayout()

        add(panel, BorderLayout.CENTER)

        EventUpdateCell.addListener { repaint() }
    }

    fun paint(
        button: Int,
        dragged: Boolean,
        count: Int,
        cells: List<Cell<Any>> = selectedCells,
        tool: Tool = Tool.current,
    ) {
        RawkyPlugin.document?.let { doc -> if (doc.selectedIndex >= doc.children.size) return }

        for (cell in cells) {
            if (cell.grid.layer.lock) continue

            tool.perform(
                cell,
                button,
                dragged,
                count,
            )

            EventUpdateCell.trigger(cell)
        }
    }
}
