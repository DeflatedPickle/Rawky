/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.eraser

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
    value = "eraser",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An eraser
    """,
    type = PluginType.OTHER,
    dependencies = ["deflatedpickle@core#1.0.0"],
)
object EraserPlugin :
    Tool(
        name = "Eraser",
        icon = MonoIcon.ERASER,
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
        val action =
            object : Action(name) {
                val old = cell.content

                override fun perform() {
                    CellProvider.current.redact(cell, button, dragged, clickCount)
                }

                override fun cleanup() {
                    CellProvider.current.cleanup(old, cell, button, dragged, clickCount)
                }

                override fun outline(g2D: Graphics2D) {}
            }

        ActionStack.push(action)
    }
}
