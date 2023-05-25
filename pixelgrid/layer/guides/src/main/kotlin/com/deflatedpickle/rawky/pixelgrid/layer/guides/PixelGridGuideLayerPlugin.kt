/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.guides

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Stroke

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_guide_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints guides upon the pixel grid
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = GuideSettings::class
)
@Suppress("unused")
object PixelGridGuideLayerPlugin : PaintLayer {
    override val name = "Guide"
    override val layer = LayerCategory.OVER_DECO

    init {
        registry["guide"] = this
    }

    override fun paint(doc: RawkyDocument?, frame: Frame?, layer: Layer?, g2d: Graphics2D) {
        val settings = ConfigUtil.getSettings<GuideSettings>("deflatedpickle@pixel_grid_guide_layer#*")

        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            settings?.let {
                drawGuides(g2d, doc, it.colour, BasicStroke(it.thickness))
            }
        }
    }

    private fun drawGuides(g: Graphics2D, doc: RawkyDocument, colour: Color, stroke: Stroke) {
        g.color = colour
        g.stroke = stroke

        for (i in doc.guides) {
            g.drawString(i.name, i.x * Grid.pixel + 4, i.y * Grid.pixel + g.fontMetrics.height + 4)
            g.drawRect(i.x * Grid.pixel, i.y * Grid.pixel, i.width * Grid.pixel, i.height * Grid.pixel)
        }
    }
}
