/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.asciipalette

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.functions.extensions.div
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.font.GlyphVector
import java.io.File

@Plugin(
    value = "ascii_palette",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a picker for ASCII characters
    """,
    type = PluginType.COMPONENT,
    component = ASCIIPalettePanel::class,
)
@Suppress("unused")
object ASCIIPalettePlugin {
    val folder = (File(".") / "palette" / "ascii").apply { mkdirs() }
    val registry = Registry<String, PaletteParser<GlyphVector>>()

    init {
        EventChangeTheme.addListener { ASCIIPalettePanel.updateUIRecursively() }
    }
}
