package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.util.DrawUtil
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

@RedrawActive
object PixelGridPanel : PluginPanel() {
    init {
        object : MouseAdapter() {
            fun click(e: MouseEvent, dragged: Boolean) {
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]
                    val grid = layer.child

                    PixelGridPanel.mousePosition?.let { mp ->
                        for (cell in grid.children) {
                            if (cell.polygon.contains(mp)) {
                                Tool.current.perform(
                                    cell,
                                    e.button,
                                    dragged,
                                    e.clickCount,
                                )

                                EventUpdateCell.trigger(cell)
                            }
                        }
                    }
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                click(e, false)
            }

            override fun mouseDragged(e: MouseEvent) {
                click(e, true)
            }
        }.apply {
            addMouseListener(this)
            addMouseMotionListener(this)
        }

        EventUpdateCell.addListener {
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            val grid = layer.child

            DrawUtil.paintGrid(g, grid)
            mousePosition?.let { DrawUtil.paintHoverCell(it, g as Graphics2D, grid) }
        }
    }
}