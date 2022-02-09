@file:Suppress("unused")

package com.deflatedpickle.rawky.tools.eraser

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.undulation.functions.extensions.toColour
import java.awt.Color
import java.awt.Point

@Plugin(
    value = "eraser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An eraser
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ]
)
object EraserPlugin : Tool {
    override val name = "eraser"
    override val icon = MonoIcon.ERASER

    init {
        Tool.registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell,
        button: Int,
        dragged: Boolean,
        clickCount: Int
    ) {
        cell.colour = Cell.defaultColour
    }
}