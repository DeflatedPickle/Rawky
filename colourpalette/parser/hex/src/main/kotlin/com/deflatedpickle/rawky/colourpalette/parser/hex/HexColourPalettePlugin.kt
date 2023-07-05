/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.hex

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import so.jabber.FileUtils
import java.awt.Color
import java.io.File

@Plugin(
    value = "hex_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses Hex palettes to colour palettes
    """,
)
@Suppress("unused")
object HexColourPalettePlugin : PaletteParser<Color> {
    override val name = "HEX"

    init {
        ColourPalettePlugin.registry["hex"] = this

        FileUtils.copyResourcesRecursively(
            HexColourPalettePlugin::class.java.getResource("/palette/community"),
            ColourPalettePlugin.folder
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        file.readLines().drop(1).forEach { line ->
            colours[Color.decode("#$line")] = null
        }

        return Palette(
            file.nameWithoutExtension,
            colours
        )
    }
}
