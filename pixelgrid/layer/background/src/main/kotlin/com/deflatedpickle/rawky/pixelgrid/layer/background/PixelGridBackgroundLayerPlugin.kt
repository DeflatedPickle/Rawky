/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.background

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Color
import java.awt.Graphics2D

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_background_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints a pseudo-transparent background
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = BackgroundSettings::class,
)
@Suppress("unused")
object PixelGridBackgroundLayerPlugin : PaintLayer {
    override val name = "Background"
    override val layer = LayerCategory.BACKGROUND

    init {
        registry["background"] = this
    }

    override fun paint(doc: RawkyDocument?, frame: Frame?, layer: Layer?, g2d: Graphics2D) {
        val settings =
            ConfigUtil.getSettings<BackgroundSettings>("deflatedpickle@pixel_grid_background_layer#*")

        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            settings?.let {
                if (it.enabled) {
                    frame?.children?.first()?.child?.let { grid ->
                        drawBackground(g2d, grid, it.fill, it.size, it.even, it.odd)
                    }
                }
            }
        }
    }

    private fun drawBackground(
        g: Graphics2D,
        grid: Grid,
        fillType: FillType,
        size: Int,
        evenColour: Color,
        oddColour: Color,
    ) {
        val fill =
            when (fillType) {
                FillType.ALL -> Pair(g.clipBounds.width, g.clipBounds.height)
                FillType.GRID -> Pair(grid.rows, grid.columns)
            }

        for (r in 0 until fill.first * Grid.pixel / size) {
            for (c in 0 until fill.second * Grid.pixel / size) {
                g.color = if (r % 2 == c % 2) evenColour else oddColour
                g.fillRect(c * size, r * size, size, size)
            }
        }
    }
}
