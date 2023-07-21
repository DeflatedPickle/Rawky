/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.grid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
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
    value = "grid_lines_paint_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints the lines of a grid
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = DivideSettings::class,
)
@Suppress("unused")
object GridLinesPaintLayerPlugin : PaintLayer {
    override val name = "Grid Lines"
    override val layer = LayerCategory.OVER_DECO

    init {
        registry["grid_lines"] = this
    }

    override fun paint(doc: RawkyDocument?, frame: Int, layer: Int, g2d: Graphics2D) {
        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            ConfigUtil.getSettings<DivideSettings>("deflatedpickle@grid_lines_paint_layer#*")?.let { settings ->
                if (!settings.enabled) return

                val f = doc.children[frame]
                val l = f.children[layer]

                DrawUtil.paintGridOutline(
                    g2d,
                    l.child,
                    settings.colour,
                    BasicStroke(settings.thickness),
                )
            }
        }
    }
}
