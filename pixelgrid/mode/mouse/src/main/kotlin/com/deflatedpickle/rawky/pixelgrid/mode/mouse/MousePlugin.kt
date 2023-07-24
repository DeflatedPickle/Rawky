/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.deflatedpickle.rawky.pixelgrid.mode.mouse

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import java.awt.MouseInfo
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

@Plugin(
    value = "mouse",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Use a mouse to paint
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object MousePlugin : Mode("Mouse", 1) {
    private val adapter =
        object : MouseAdapter() {
            fun click(e: MouseEvent, dragged: Boolean) {
                RawkyPlugin.document?.let { doc ->
                    if (doc.selectedIndex >= doc.children.size) return

                    PixelGridPanel.paint(
                        e.button,
                        dragged,
                        e.clickCount,
                    )
                }
            }

            fun move(e: MouseEvent) {
                RawkyPlugin.document?.let { doc ->
                    if (doc.selectedIndex >= doc.children.size) return

                    val frame = doc.children[doc.selectedIndex]
                    val layer = frame.children[frame.selectedIndex]
                    val grid = layer.child

                    MouseInfo.getPointerInfo()?.let {
                        val point = MouseInfo.getPointerInfo().location
                        SwingUtilities.convertPointFromScreen(point, PixelGridPanel.panel)

                        PixelGridPanel.selectedCells.clear()

                        for (cell in grid.children) {
                            if (cell.polygon.contains(point)) {
                                PixelGridPanel.selectedCells.add(cell)
                                break
                            }
                        }
                    }
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                click(e, false)
            }

            override fun mouseDragged(e: MouseEvent) {
                move(e)
                click(e, true)
            }

            override fun mouseMoved(e: MouseEvent) {
                move(e)
            }
        }

    override fun apply() {
        adapter.apply {
            PixelGridPanel.addMouseListener(this)
            PixelGridPanel.addMouseMotionListener(this)
        }
    }

    override fun remove() {
        adapter.apply {
            PixelGridPanel.removeMouseListener(this)
            PixelGridPanel.removeMouseMotionListener(this)
        }
    }

    init {
        registry[name] = this
    }
}
