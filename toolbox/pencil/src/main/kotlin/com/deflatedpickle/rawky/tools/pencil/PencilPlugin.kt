@file:Suppress("unused")

package com.deflatedpickle.rawky.tools.pencil

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.undulation.functions.extensions.toColour
import java.awt.Color
import java.awt.Point

@Plugin(
    value = "pencil",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A pencil
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ]
)
object PencilPlugin : Tool {
    override val name = "pencil"
    override val icon = MonoIcon.PENCIL

    init {
        Tool.registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell,
        button: Int,
        dragged: Boolean,
        clickCount: Int
    ) {
        cell.colour = RawkyPlugin.colour
    }
}