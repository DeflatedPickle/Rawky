/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection", "unused")

package com.deflatedpickle.rawky.tilepalette.parser.png

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.rawky.api.palette.Palette
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.tilepalette.ImportTilePaletteDialog
import com.deflatedpickle.rawky.tilepalette.TileMapInfo
import com.deflatedpickle.rawky.tilepalette.TilePalettePlugin
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.oxbow.swingbits.dialog.task.TaskDialog
import so.jabber.FileUtils
import java.awt.Image
import java.awt.Point
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
@OptIn(InternalSerializationApi::class)
object PNGTilePalettePlugin : PaletteParser<Image> {
    override val name = "PNG"

    init {
        TilePalettePlugin.registry["png"] = this

        FileUtils.copyResourcesRecursively(
            PNGTilePalettePlugin::class.java.getResource("/tilemap"),
            TilePalettePlugin.folder,
        )
    }

    override fun parse(file: File): Palette<Image> {
        val tiles = mutableMapOf<Image, String?>()

        var tileMapInfo: TileMapInfo? = null

        var ready = false

        val infoFile = file.parentFile.resolve("${file.nameWithoutExtension}.json")

        // reads an accociated file for the width and height
        if (infoFile.exists()) {
            tileMapInfo =
                Haruhi.json.decodeFromString(TileMapInfo::class.serializer(), infoFile.readText())

            ready = true
        }
        // opens a dialog for the user to input width and height
        else {
            val dialog = ImportTilePaletteDialog()
            dialog.isVisible = true

            if (dialog.result == TaskDialog.StandardCommand.OK) {
                tileMapInfo =
                    TileMapInfo(
                        Point(dialog.tileWidthInput.value as Int, dialog.tileHeightInput.value as Int),
                    )

                ready = true
            }
        }

        if (ready && tileMapInfo != null) {
            val img = ImageIO.read(file)

            val width = tileMapInfo.size.x
            val height = tileMapInfo.size.y

            // loops tiles in the image
            for (h in 0 until img.height / height) {
                for (w in 0 until img.width / width) {
                    val tile = img.getSubimage(width * w, height * h, width, height)
                    val colours = mutableListOf<Int>()

                    // adds the colours of the image to a list
                    for (th in 0 until tile.height) {
                        for (tw in 0 until tile.width) {
                            colours.add(tile.getRGB(tw, th))
                        }
                    }

                    // ignores the tile if its a solid colour
                    // TODO: add placeholders so multi tile objects keep their shape
                    if (colours.all { it == colours.first() }) continue

                    // TODO: store names for the tiles?
                    tiles[tile] = null
                }
            }
        }

        return Palette(file.nameWithoutExtension, tiles)
    }
}
