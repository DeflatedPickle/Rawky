/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.pal

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "pal_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses JASC Pal palettes to colour palettes
    """,
)
@Suppress("unused")
object PALColourPalettePlugin : PaletteParser<Color> {
    override val name = "PAL"

    init {
        ColourPalettePlugin.registry["pal"] = this

        FileUtils.copyResourcesRecursively(
            PALColourPalettePlugin::class.java.getResource("/palette/community"),
            ColourPalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        file.readLines().drop(3).forEach { line ->
            val (r, g, b) = line.split(" ").map { it.toInt() }
            colours[Color(r, g, b)] = null
        }

        return Palette(file.nameWithoutExtension, colours)
    }
}
