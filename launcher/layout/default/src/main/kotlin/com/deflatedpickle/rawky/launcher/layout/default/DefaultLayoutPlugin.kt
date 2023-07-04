@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.default

import ModernDocking.DockingRegion
import ModernDocking.DockingState
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import ModernDocking.persist.AppState
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import javax.swing.SwingUtilities

@Plugin(
    value = "default_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Sets the default positions of the default components
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@colour_palette#*",
        "deflatedpickle@colour_wheel#*",
        "deflatedpickle@colour_shades#*",
        "deflatedpickle@layer_list#*",
    ],
)
object DefaultLayoutPlugin {
    init {
        val layout = WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock("deflatedpickle@colour_wheel", "deflatedpickle@pixel_grid", DockingRegion.WEST, 0.2)
            .dock("deflatedpickle@colour_shades", "deflatedpickle@colour_wheel", DockingRegion.SOUTH, 0.2)
            .dock("deflatedpickle@colour_palette", "deflatedpickle@colour_wheel", DockingRegion.SOUTH, 0.2)
            .dock("deflatedpickle@layer_list", "deflatedpickle@pixel_grid", DockingRegion.EAST, 0.2)
            .buildApplicationLayout()

        DockingLayouts.addLayout("default", layout)
        AppState.setDefaultApplicationLayout(layout)

        EventProgramFinishSetup.addListener {
            AppState.restore()
        }
    }
}