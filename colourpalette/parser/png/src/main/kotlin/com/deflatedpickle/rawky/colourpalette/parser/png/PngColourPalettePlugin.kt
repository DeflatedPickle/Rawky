/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette.parser.png

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.colourpalette.ColourPalettePlugin
import com.deflatedpickle.rawky.colourpalette.api.Palette
import com.deflatedpickle.rawky.colourpalette.api.PaletteParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import so.jabber.FileUtils
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
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
object PngColourPalettePlugin : PaletteParser {
    init {
        PaletteParser.registry["png"] = this

        FileUtils.copyResourcesRecursively(
            PngColourPalettePlugin::class.java.getResource("/palette/scheme"),
            ColourPalettePlugin.folder
        )
    }

    override fun parse(file: File): Palette {
        val colours = mutableMapOf<Color, String?>()

        val img = ImageIO.read(file)

        for (col in 0 until img.width) {
            for (row in 0 until img.height) {
                val c = Color(img.getRGB(col, row))
                if (c == Color.WHITE) continue
                colours[c] = null
            }
        }

        return Palette(
            file.nameWithoutExtension,
            colours
        )
    }
}
