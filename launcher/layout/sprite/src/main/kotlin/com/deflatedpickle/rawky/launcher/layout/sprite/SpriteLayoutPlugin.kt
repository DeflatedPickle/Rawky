/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.launcher.layout.sprite

import ModernDocking.DockingRegion
import ModernDocking.DockingState
import ModernDocking.layouts.ApplicationLayout
import ModernDocking.layouts.DockingLayouts
import ModernDocking.layouts.WindowLayoutBuilder
import ModernDocking.persist.AppState
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import com.deflatedpickle.rawky.launcher.gui.ToolBar

@Plugin(
    value = "sprite_layout",
    author = "DeflatedPickle",
    version = "1.0.0",
    description =
    """
        <br>
        Sets the default positions of the default components
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@launcher#*",
        "deflatedpickle@pixel_grid#*",
        "deflatedpickle@colour_palette#*",
        "deflatedpickle@colour_wheel#*",
        "deflatedpickle@colour_shades#*",
        "deflatedpickle@colour_history#*",
        "deflatedpickle@layer_list#*",
    ],
)
object SpriteLayoutPlugin {
    private val layout: ApplicationLayout =
        WindowLayoutBuilder("deflatedpickle@pixel_grid")
            .dock(
                "deflatedpickle@colour_wheel",
                "deflatedpickle@pixel_grid",
                DockingRegion.WEST,
                0.3,
            )
            .dock(
                "deflatedpickle@colour_palette",
                "deflatedpickle@colour_wheel",
                DockingRegion.SOUTH,
                0.5,
            )
            .dock(
                "deflatedpickle@colour_shades",
                "deflatedpickle@colour_palette",
                DockingRegion.SOUTH,
                0.0,
            )
            .dock(
                "deflatedpickle@colour_history",
                "deflatedpickle@colour_shades",
                DockingRegion.EAST,
                0.5,
            )
            .dock(
                "deflatedpickle@layer_list",
                "deflatedpickle@pixel_grid",
                DockingRegion.EAST,
                0.3,
            )
            .buildApplicationLayout()

    init {
        DockingLayouts.addLayout("sprite", layout)
        AppState.setDefaultApplicationLayout(layout)

        EventProgramFinishSetup.addListener {
            AppState.restore()
            ToolBar.layoutComboBox.selectedItem = "Sprite"
        }

        EventCreateDocument.addListener {
            loadLayout()
        }

        EventOpenDocument.addListener {
            loadLayout()
        }
    }

    private fun loadLayout() {
        RawkyPlugin.document?.let {
            if (it.cellProvider == PixelCellPlugin && it.children.size == 1) {
                DockingState.restoreApplicationLayout(layout)
                ToolBar.layoutComboBox.selectedItem = "Sprite"
            }
        }
    }
}
