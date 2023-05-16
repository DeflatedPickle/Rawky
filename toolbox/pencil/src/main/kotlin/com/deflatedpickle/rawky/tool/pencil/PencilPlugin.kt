/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.pencil

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.Graphics2D

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
) {
    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell<out Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        val action = object : Action(name) {
            // TODO
            val old = cell.content

            override fun perform() {
                CellProvider.current.perform(
                    cell, button, dragged, clickCount
                )
            }

            override fun cleanup() {
                CellProvider.current.cleanup(
                    old, cell, button, dragged, clickCount
                )
            }

            override fun outline(g2D: Graphics2D) {
            }
        }

        ActionStack.push(action)
    }
}
