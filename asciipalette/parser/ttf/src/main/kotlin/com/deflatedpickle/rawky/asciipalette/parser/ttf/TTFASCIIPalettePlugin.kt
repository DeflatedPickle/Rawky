/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.asciipalette.parser.ttf

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.asciipalette.ASCIIPalettePlugin
import so.jabber.FileUtils
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.geom.AffineTransform
import java.io.File

@Plugin(
    value = "ascii_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses Gimp palettes to colour palettes
    """,
)
@Suppress("unused")
object TTFASCIIPalettePlugin : PaletteParser<GlyphVector> {
    override val name = "TTF"

    init {
        ASCIIPalettePlugin.registry["ttf"] = this

        FileUtils.copyResourcesRecursively(
            ASCIIPalettePlugin::class.java.getResource("/fonts"),
            ASCIIPalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<GlyphVector> {
        val colours = mutableMapOf<GlyphVector, String?>()

        val f = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(16f)

        for (i in Char.MIN_VALUE..Char.MAX_VALUE) {
            if (f.canDisplay(i)) {
                colours[f.createGlyphVector(
                    FontRenderContext(
                        AffineTransform(),
                        true,
                        true
                    ), charArrayOf(i)
                )] = i.toString()
            }
        }

        return Palette(file.nameWithoutExtension, colours)
    }
}
