@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourwheel

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "colour_wheel",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a wheel to select a colour
    """,
    type = PluginType.COMPONENT,
    component = ColourWheelPanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
)
@Suppress("unused")
object ColourWheelPlugin {
    init {
        EventChangeTheme.addListener {
            ColourWheelPanel.updateUIRecursively()
        }

        EventChangeColour.addListener {
            ColourWheelPanel.colourPicker.color = it
        }
    }
}