@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.pencil

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import com.deflatedpickle.rawky.util.ActionStack.MultiAction
import java.awt.Graphics2D
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
object PencilPlugin : Tool(
    name = "Pencil",
    icon = MonoIcon.PENCIL,
    offset = { _, y ->
        Point(
            2,
            y - 1,
        )
    }
) {
    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        val action = object : Action(name) {
            val old = cell.colour

            override fun perform() {
                cell.colour = RawkyPlugin.colour
            }

            override fun cleanup() {
                cell.colour = old
            }

            override fun outline(g2D: Graphics2D) {
            }
        }

        ActionStack.push(action)
    }
}