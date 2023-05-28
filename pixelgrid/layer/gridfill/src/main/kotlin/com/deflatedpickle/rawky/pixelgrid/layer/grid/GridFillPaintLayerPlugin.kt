/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.grid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.DrawUtil
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Graphics2D

@ExperimentalSerializationApi
@Plugin(
    value = "grid_fill_paint_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints the fill of a grid
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
)
@Suppress("unused")
object GridFillPaintLayerPlugin : PaintLayer {
    override val name = "Grid Fill"
    override val layer = LayerCategory.GRID

    init {
        registry["grid_fill"] = this
    }

    override fun paint(
        doc: RawkyDocument?,
        frame: Frame?,
        layer: Layer?,
        g2d: Graphics2D
    ) {
        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            frame?.let {
                for (l in frame.children) {
                    if (l.visible) {
                        DrawUtil.paintGridFill(g2d, l.child)
                    }
                }
            }
        }
    }
}
