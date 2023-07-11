/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.txt

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "txt_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses Paint.NET palettes to colour palettes
    """,
)
@Suppress("unused")
object TXTColourPalettePlugin : PaletteParser<Color> {
    override val name = "TXT"

    init {
        ColourPalettePlugin.registry["txt"] = this

        FileUtils.copyResourcesRecursively(
            TXTColourPalettePlugin::class.java.getResource("/palette/community"),
            ColourPalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        file.readLines().forEach { line ->
            if (!line.startsWith(";")) {
                colours[Color(line.toLong(16).toInt(), true)] = null
            }
        }

        return Palette(file.nameWithoutExtension, colours)
    }
}
