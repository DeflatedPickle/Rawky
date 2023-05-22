/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourshades

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "colour_shades",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a widget to show darker/lighter shades of the current colour
    """,
    type = PluginType.COMPONENT,
    component = ColourShadesPanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
    settings = ColourShadesSettings::class
)
@Suppress("unused")
object ColourShadesPlugin {
    init {
        EventChangeTheme.addListener {
            ColourShadesPanel.updateUIRecursively()
        }

        EventChangeColour.addListener {
            ColourShadesPanel.createShades()
        }

        EventProgramFinishSetup.addListener {
            ColourShadesPanel.createShades()
        }
    }
}
