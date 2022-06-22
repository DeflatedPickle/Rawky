@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.mode.keyboard

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import java.awt.Point
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent


@Plugin(
    value = "keyboard",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Use a keyboard to paint
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@pixel_grid#*",
    ],
    settings = KeyboardSettings::class,
)
object KeyboardPlugin : Mode("Keyboard") {
    val point = Point(0, 0)

    private val adapter = object : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            RawkyPlugin.document?.let { doc ->
                val frame = doc.children[doc.selectedIndex]
                val layer = frame.children[frame.selectedIndex]
                val grid = layer.child

                ConfigUtil.getSettings<KeyboardSettings>("deflatedpickle@keyboard#*")?.let {
                    when (e.keyCode) {
                        it.negativeY -> if (point.y - 1 >= 0) {
                            point.translate(0, -1)
                        }
                        it.positiveY -> if (point.y + 1 <= grid.rows) {
                            point.translate(0, 1)
                        }
                        it.negativeX -> if (point.x - 1 >= 0) {
                            point.translate(-1, 0)
                        }
                        it.positiveX -> if (point.x + 1 <= grid.columns) {
                            point.translate(1, 0)
                        }
                        it.useTool -> PixelGridPanel.paint(
                            MouseEvent.BUTTON1,
                            e.isShiftDown,
                            1,
                        )
                    }
                }

                if (!e.isShiftDown) {
                    PixelGridPanel.selectedCells.clear()
                }

                PixelGridPanel.selectedCells.add(grid[point.x, point.y])
            }
        }
    }

    override fun apply() {
        PixelGridPanel.addKeyListener(adapter)
    }

    override fun remove() {
        PixelGridPanel.removeKeyListener(adapter)
    }

    init {
        registry[name] = this
    }
}