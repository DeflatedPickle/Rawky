/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.tilemap

import ModernDocking.DockingRegion
import ModernDocking.DockingState
import ModernDocking.layouts.ApplicationLayout
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.grid.tile.TileCellPlugin
import com.deflatedpickle.rawky.launcher.gui.ToolBar

@Plugin(
    value = "tilemap_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description =
    """
        <br>
        Sets the default positions of the components to make tile maps
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@layer_list#*",
        "deflatedpickle@tile_palette#*",
        "deflatedpickle@tiled_view#*",
    ],
)
object TileMapLayoutPlugin {
    private val layout: ApplicationLayout =
        WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock(
                "deflatedpickle@tile_palette",
                "deflatedpickle@pixel_grid",
                DockingRegion.WEST,
                0.3,
            )
            .dock(
                "deflatedpickle@layer_list",
                "deflatedpickle@pixel_grid",
                DockingRegion.EAST,
                0.3,
            )
            .dock(
                "deflatedpickle@tiled_view",
                "deflatedpickle@layer_list",
                DockingRegion.SOUTH,
                0.5,
            )
            .buildApplicationLayout()

    init {
        DockingLayouts.addLayout("tilemap", layout)

        EventCreateDocument.addListener {
            loadLayout()
        }

        EventOpenDocument.addListener {
            loadLayout()
        }
    }

    private fun loadLayout() {
        RawkyPlugin.document?.let {
            if (it.cellProvider == TileCellPlugin) {
                DockingState.restoreApplicationLayout(layout)
                ToolBar.layoutComboBox.selectedItem = "Tilemap"
            }
        }
    }
}
