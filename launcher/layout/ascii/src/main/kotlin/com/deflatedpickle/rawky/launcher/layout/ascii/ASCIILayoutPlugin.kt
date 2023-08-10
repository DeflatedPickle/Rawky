/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.ascii

import ModernDocking.Docking
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
import com.deflatedpickle.rawky.grid.ascii.ASCIICellPlugin
import com.deflatedpickle.rawky.launcher.gui.ToolBar

@Plugin(
    value = "animation_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Adds a layout with ASCII art related components
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@colour_wheel#*",
        "deflatedpickle@layer_list#*",
        "deflatedpickle@timeline#*",
        "deflatedpickle@ascii_palette#*",
    ],
)
object ASCIILayoutPlugin {
    private val layout: ApplicationLayout =
        WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock(
                "deflatedpickle@timeline",
                "deflatedpickle@pixel_grid",
                DockingRegion.SOUTH,
                0.3)
            .dock(
                "deflatedpickle@layer_list",
                "deflatedpickle@pixel_grid",
                DockingRegion.EAST,
                0.2
            )
            .dock(
                "deflatedpickle@colour_wheel",
                "deflatedpickle@pixel_grid",
                DockingRegion.WEST,
                0.4,
            )
            .dock(
                "deflatedpickle@ascii_palette",
                "deflatedpickle@colour_wheel",
                DockingRegion.SOUTH,
                0.5,
            )
            .buildApplicationLayout()

    init {
        DockingLayouts.addLayout("ascii", layout)

        EventCreateDocument.addListener {
            loadLayout()
        }

        EventOpenDocument.addListener {
            loadLayout()
        }
    }

    private fun loadLayout() {
        RawkyPlugin.document?.let {
            if (it.cellProvider == ASCIICellPlugin) {
                DockingState.restoreApplicationLayout(layout)
                ToolBar.layoutComboBox.selectedItem = "Ascii"
            }
        }
    }
}
