/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.layer.debug

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.setting.RawkySettings
import com.deflatedpickle.undulation.functions.extensions.getContrast
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.TextLayout
import java.awt.geom.AffineTransform

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

            if (Haruhi.isInDev) {
                drawDebug(g2d, doc)
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

            g.drawOutlinedString("grid: ${grid.rows}x${grid.columns}", 5, g.fontMetrics.height + 5)
            g.drawOutlinedString("frame: ${doc.selectedIndex}", 5, g.fontMetrics.height * 2 + 5)
            g.drawOutlinedString("layer: ${frame.selectedIndex}", 5, g.fontMetrics.height * 3 + 5)

            g.drawOutlinedString("visible: ${layer.visible}", 5, g.fontMetrics.height * 5 + 5)
            g.drawOutlinedString("lock: ${layer.lock}", 5, g.fontMetrics.height * 6 + 5)
        }
    }

    private fun Graphics2D.drawOutlinedString(string: String, x: Int, y: Int) {
        val cached = color

        setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON)
        setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY)

        transform = AffineTransform.getTranslateInstance(x.toDouble(), y.toDouble())

        val tl = TextLayout(
                string,
                font,
                fontRenderContext
            )

        val shape = tl.getOutline(null)

        color = cached.getContrast()
        stroke = BasicStroke(4f)
        draw(shape)
        color = cached
        fill(shape)
    }
}
