/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.tiledview

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "tiled_view",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A component to show the current frame tiled in a 3x3 square
    """,
    type = PluginType.COMPONENT,
    component = TiledViewPanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
    settings = TiledViewSettings::class,
)
object TiledViewPlugin {
    init {
        EventChangeTheme.addListener {
            TiledViewPanel.updateUIRecursively()
        }

        EventUpdateGrid.addListener {
            TiledViewPanel.tilePanel.repaint()
        }

        EventUpdateCell.addListener {
            TiledViewPanel.tilePanel.repaint()
        }
    }
}
