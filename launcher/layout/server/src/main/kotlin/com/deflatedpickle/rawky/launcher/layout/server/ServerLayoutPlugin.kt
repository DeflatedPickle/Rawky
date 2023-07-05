/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.server

import ModernDocking.DockingRegion
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType

@Plugin(
    value = "server_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Sets the default positions of the components to paint with friends
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@colour_wheel#*",
        "deflatedpickle@layer_list#*",
        "deflatedpickle@chat#*",
        "deflatedpickle@userlist#*",
        "deflatedpickle@leaderboard#*",
    ],
)
object ServerLayoutPlugin {
    init {
        val layout = WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock("deflatedpickle@chat", "deflatedpickle@pixel_grid", DockingRegion.SOUTH, 0.4)
            .dock("deflatedpickle@colour_wheel", "deflatedpickle@pixel_grid", DockingRegion.WEST, 0.2)
            .dock("deflatedpickle@layer_list", "deflatedpickle@pixel_grid", DockingRegion.EAST)
            .dock("deflatedpickle@userlist", "deflatedpickle@layer_list", DockingRegion.EAST, 0.8)
            .dock("deflatedpickle@leaderboard", "deflatedpickle@userlist")
            .buildApplicationLayout()

        DockingLayouts.addLayout("server", layout)
    }
}
