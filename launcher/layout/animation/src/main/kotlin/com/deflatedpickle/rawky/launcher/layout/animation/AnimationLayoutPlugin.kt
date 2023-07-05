/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.animation

import ModernDocking.DockingRegion
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType

@Plugin(
    value = "animation_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Adds a layout with animation related components
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@colour_wheel#*",
        "deflatedpickle@layer_list#*",
        "deflatedpickle@timeline#*",
        "deflatedpickle@animation_preview#*",
    ],
)
object AnimationLayoutPlugin {
    init {
        val layout = WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock("deflatedpickle@timeline", "deflatedpickle@pixel_grid", DockingRegion.SOUTH, 0.4)
            .dock("deflatedpickle@colour_wheel", "deflatedpickle@pixel_grid", DockingRegion.WEST, 0.2)
            .dock("deflatedpickle@layer_list", "deflatedpickle@pixel_grid", DockingRegion.EAST)
            .dock("deflatedpickle@animation_preview", "deflatedpickle@layer_list", DockingRegion.NORTH, 0.8)
            .buildApplicationLayout()

        DockingLayouts.addLayout("animation", layout)
    }
}
