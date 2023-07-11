/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.gpl

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "gpl_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses Gimp palettes to colour palettes
    """,
)
@Suppress("unused")
object GPLColourPalettePlugin : PaletteParser<Color> {
    override val name = "GPL"

    init {
        ColourPalettePlugin.registry["gpl"] = this

        FileUtils.copyResourcesRecursively(
            GPLColourPalettePlugin::class.java.getResource("/palette/community"),
            ColourPalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        var pastHashtag = false

        file.readLines().forEach { line ->
            if (pastHashtag) {
                val split = line.split("\t", " ").filter { it.isNotBlank() }

                val red = split[0].toInt()
                val green = split[1].toInt()
                val blue = split[2].toInt()

                val comment = split.drop(3).joinToString(" ")

                colours[Color(red, green, blue)] = comment
            } else if (line.startsWith("#")) {
                pastHashtag = true
            }
        }

        return Palette(file.nameWithoutExtension, colours)
    }
}
