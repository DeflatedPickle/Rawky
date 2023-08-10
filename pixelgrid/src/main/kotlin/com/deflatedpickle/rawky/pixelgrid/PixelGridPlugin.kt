/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateGrid
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    version = "1.0.0",
    description =
    """
        Provides a grid to draw upon
        <br>
        This plugin provides most of the basic user functionality of Rawky.
    """,
    type = PluginType.COMPONENT,
    component = PixelGridPanel::class,
    dependencies =
    [
        "deflatedpickle@core#*",
    ],
)
@Suppress("unused")
object PixelGridPlugin {
    init {
        EventCreateDocument.addListener { PixelGridPanel.repaint() }

        EventOpenDocument.addListener { PixelGridPanel.repaint() }

        EventUpdateGrid.addListener { PixelGridPanel.repaint() }

        EventChangeTool.addListener { PixelGridPanel.repaint() }
    }
}
