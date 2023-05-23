/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.onionskin

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.pixelgrid.api.Layer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_onion_skin_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints the last and next frames in different colours
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = OnionSkinSettings::class,
)
@Suppress("unused")
object PixelGridOnionSkinLayerPlugin : PaintLayer {
    override val name = "OnionSkin"
    override val layer = Layer.UNDER_DECO

    init {
        registry["onionskin"] = this
    }

    override fun paint(g2d: Graphics2D) {
        RawkyPlugin.document?.let { doc ->
            if (doc.selectedIndex >= doc.children.size) return

            ConfigUtil.getSettings<OnionSkinSettings>("deflatedpickle@pixel_grid_onion_skin_layer#*")?.let {
                if (!it.enabled) return

                if (it.skinStratergy == SkinStratergy.MONOTONE) {
                    g2d.color = it.previousColour
                }

                for (i in 1 .. it.previousFrames) {
                    if (doc.selectedIndex - i >= 0) {
                        g2d.composite = AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            1f / ((i / it.previousFrames + 1) + 1f)
                        )

                        for (layer in doc.children[doc.selectedIndex - i].children) {
                            val grid = layer.child

                            if (layer.visible) {
                                for (cell in grid.children) {
                                    if (cell.content != CellProvider.current.default) {
                                        if (it.skinStratergy == SkinStratergy.COLOUR &&
                                            CellProvider.current.current is Color) {
                                            g2d.color = cell.content as Color
                                        }

                                        g2d.fillRect(
                                            cell.polygon.x, cell.polygon.y,
                                            cell.polygon.width, cell.polygon.height
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (it.skinStratergy == SkinStratergy.MONOTONE) {
                    g2d.color = it.futureColour
                }

                for (i in 1 .. it.futureFrames) {
                    if (doc.selectedIndex + i < doc.children.size) {
                        g2d.composite = AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            1f / ((i / it.futureFrames + 1) + 1f)
                        )

                        for (layer in doc.children[doc.selectedIndex + i].children) {
                            val grid = layer.child

                            if (layer.visible) {
                                for (cell in grid.children) {
                                    if (cell.content != CellProvider.current.default) {
                                        if (it.skinStratergy == SkinStratergy.COLOUR &&
                                            CellProvider.current.current is Color) {
                                            g2d.color = cell.content as Color
                                        }

                                        g2d.fillRect(
                                            cell.polygon.x, cell.polygon.y,
                                            cell.polygon.width, cell.polygon.height
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
