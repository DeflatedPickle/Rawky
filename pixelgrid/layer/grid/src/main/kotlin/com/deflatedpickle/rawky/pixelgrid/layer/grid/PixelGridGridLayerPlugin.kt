/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.grid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.DrawUtil
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.BasicStroke
import java.awt.Graphics2D

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_grid_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints the frames and layers of a grid
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = DivideSettings::class
)
@Suppress("unused")
object PixelGridGridLayerPlugin : PaintLayer {
    override val name = "Grid"
    override val layer = LayerCategory.GRID

    init {
        registry["grid"] = this
    }

    override fun paint(
        doc: RawkyDocument?,
        frame: Frame?,
        layer: Layer?,
        g2d: Graphics2D
    ) {
        val settings = ConfigUtil.getSettings<DivideSettings>("deflatedpickle@pixel_grid_grid_layer#*")

        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            frame?.let { l ->
                for (l in frame.children) {
                    val grid = l.child

                    settings?.let {
                        if (l.visible) {
                            DrawUtil.paintGridFill(g2d, grid)
                        }

                        DrawUtil.paintGridOutline(g2d, grid, settings.colour, BasicStroke(settings.thickness))
                    }
                }
            }
        }
    }
}
