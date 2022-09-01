@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition
import com.deflatedpickle.marvin.extensions.div
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.io.File

@Plugin(
    value = "colour_palette",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a picker for colour palettes
    """,
    type = PluginType.COMPONENT,
    component = ColourPalettePanel::class,
    componentVisible = false,
    componentMinimizedPosition = ComponentPosition.WEST,
)
@Suppress("unused")
object ColourPalettePlugin {
    val folder = (File(".") / "palette").apply { mkdirs() }

    init {
        EventChangeTheme.addListener {
            ColourPalettePanel.updateUIRecursively()
        }
    }
}