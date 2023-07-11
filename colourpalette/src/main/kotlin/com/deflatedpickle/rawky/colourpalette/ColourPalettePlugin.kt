/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.extensions.div
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.Color
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
)
@Suppress("unused")
object ColourPalettePlugin {
    val folder = (File(".") / "palette").apply { mkdirs() }
    val registry = Registry<String, PaletteParser<Color>>()

    init {
        EventChangeTheme.addListener { ColourPalettePanel.updateUIRecursively() }
    }
}
