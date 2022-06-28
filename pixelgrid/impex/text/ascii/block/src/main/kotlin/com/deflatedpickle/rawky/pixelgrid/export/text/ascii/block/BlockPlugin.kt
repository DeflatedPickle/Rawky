@file:Suppress("unused", "UNUSED_PARAMETER")

package com.deflatedpickle.rawky.pixelgrid.export.text.ascii.block

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.api.Mode
import com.deflatedpickle.rawky.pixelgrid.export.text.ascii.api.Palette
import com.deflatedpickle.rawky.pixelgrid.export.text.ascii.api.Palette.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.DrawUtil
import com.github.underscore.lodash.U
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

@Plugin(
    value = "block",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a palette for block characters
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@ascii#*",
    ],
)
object BlockPlugin : Palette("Block") {
    init {
        registry[name] = this
    }

    override fun char(x: Int, y: Int, colour: Color) = when (colour.alpha) {
        0 -> " "
        in 1..64 -> "░"
        in 65..128 -> "▒"
        in 129..192 -> "▓"
        in 193..255 -> "█"
        else -> "?"
    }.repeat(2)
}