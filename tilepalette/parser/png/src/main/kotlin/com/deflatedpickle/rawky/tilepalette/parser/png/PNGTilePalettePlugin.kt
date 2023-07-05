/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.tilepalette.parser.png

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.tilepalette.ImportTilePaletteDialog
import com.deflatedpickle.rawky.tilepalette.TilePalettePlugin
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

@Plugin(
    value = "png_tile_palette_parser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Parses PNG files to tile palettes
    """,
)
object PNGTilePalettePlugin : PaletteParser<Image> {
    override val name = "PNG"

    init {
        TilePalettePlugin.registry["png"] = this
    }

    override fun parse(file: File): Palette<Image> {
        val colours = mutableMapOf<Image, String?>()

        val dialog = ImportTilePaletteDialog()
        dialog.isVisible = true

        if (dialog.result == TaskDialog.StandardCommand.OK) {
            val img = ImageIO.read(file)

            val width = dialog.tileWidthInput.value as Int
            val height = dialog.tileHeightInput.value as Int

            for (h in 0 until img.height / height) {
                for (w in 0 until img.width / width) {
                    colours[img.getSubimage(width * w, height * h, width, height)] = null
                }
            }
        }

        return Palette(
            file.nameWithoutExtension,
            colours
        )
    }
}
