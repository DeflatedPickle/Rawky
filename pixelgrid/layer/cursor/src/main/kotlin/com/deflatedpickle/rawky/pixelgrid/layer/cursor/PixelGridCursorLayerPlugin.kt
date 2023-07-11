/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.cursor

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.setting.RawkySettings
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MouseInfo
import javax.swing.SwingUtilities

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_cursor_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints a tool cursors
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
)
@Suppress("unused")
object PixelGridCursorLayerPlugin : PaintLayer {
    override val name = "Cursor"
    override val layer = LayerCategory.CURSOR

    init {
        registry["cursor"] = this
    }

    override fun paint(doc: RawkyDocument?, frame: Frame?, layer: Layer?, g2d: Graphics2D) {
        drawCursor(g2d)
    }

    private fun drawCursor(g: Graphics) {
        val settings = ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")
        val width = settings?.cursorSize?.x ?: Tool.defaultSize
        val height = settings?.cursorSize?.y ?: Tool.defaultSize

        if (Tool.isToolValid() && MouseInfo.getPointerInfo() != null) {
            val point = MouseInfo.getPointerInfo().location
            SwingUtilities.convertPointFromScreen(point, PixelGridPanel)

            g.drawImage(
                Tool.current.icon.image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING),
                point.x,
                point.y,
                null,
            )
        }
    }
}
