@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.tilemap

import ModernDocking.DockingRegion
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType

@Plugin(
    value = "tilemap_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Sets the default positions of the components to make tile maps
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@layer_list#*",
        "deflatedpickle@tile_palette#*",
    ],
)
object TileMapLayoutPlugin {
    init {
        val layout = WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock("deflatedpickle@tile_palette", "deflatedpickle@pixel_grid", DockingRegion.WEST, 0.2)
            .dock("deflatedpickle@layer_list", "deflatedpickle@pixel_grid", DockingRegion.EAST)
            .buildApplicationLayout()

        DockingLayouts.addLayout("tilemap", layout)
    }
}