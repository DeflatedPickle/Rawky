/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.debug

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.setting.RawkySettings
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Graphics2D

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_debug_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints a debug information on the pixel grid
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
)
@Suppress("unused")
object PixelGridDebugLayerPlugin : PaintLayer {
    override val name = "Debug"
    override val layer = LayerCategory.DEBUG

    init {
        registry["debug"] = this
    }

    override fun paint(doc: RawkyDocument?, frame: Int, layer: Int, g2d: Graphics2D) {
        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")?.let {
                if (it.debug.enabled) {
                    drawDebug(g2d, doc)
                }
            }
        }
    }

    private fun drawDebug(g: Graphics2D, doc: RawkyDocument) {
        val frame = doc.children[doc.selectedIndex]
        val layer = frame.children[frame.selectedIndex]
        val grid = layer.child

        ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")?.let {
            g.color = it.debug.colour
            g.font = it.debug.font
        }

        g.drawString("grid: ${grid.rows}x${grid.columns}", 5, g.fontMetrics.height + 5)
        g.drawString("frame: ${doc.selectedIndex}", 5, g.fontMetrics.height * 2 + 5)
        g.drawString("layer: ${frame.selectedIndex}", 5, g.fontMetrics.height * 3 + 5)

        g.drawString("visible: ${layer.visible}", 5, g.fontMetrics.height * 5 + 5)
        g.drawString("lock: ${layer.lock}", 5, g.fontMetrics.height * 6 + 5)
    }
}
