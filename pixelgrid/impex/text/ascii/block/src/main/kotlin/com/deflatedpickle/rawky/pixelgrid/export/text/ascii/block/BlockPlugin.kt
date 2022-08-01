/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.deflatedpickle.rawky.pixelgrid.export.text.ascii.block

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.pixelgrid.export.text.ascii.api.Palette
import java.awt.Color

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
