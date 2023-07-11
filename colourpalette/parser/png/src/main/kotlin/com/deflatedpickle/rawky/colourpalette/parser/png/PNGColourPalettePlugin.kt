/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.png

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import so.jabber.FileUtils
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

@Plugin(
    value = "png_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses PNG files to colour palettes
    """,
)
@Suppress("unused")
object PNGColourPalettePlugin : PaletteParser<Color> {
    override val name = "PNG"

    init {
        ColourPalettePlugin.registry["png"] = this

        FileUtils.copyResourcesRecursively(
            PNGColourPalettePlugin::class.java.getResource("/palette/scheme"),
            ColourPalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<Color> {
        val colours = mutableMapOf<Color, String?>()

        val img = ImageIO.read(file)

        for (col in 0 until img.width) {
            for (row in 0 until img.height) {
                val c = Color(img.getRGB(col, row))
                if (c == Color.WHITE) continue
                colours[c] = null
            }
        }

        return Palette(file.nameWithoutExtension, colours)
    }
}
