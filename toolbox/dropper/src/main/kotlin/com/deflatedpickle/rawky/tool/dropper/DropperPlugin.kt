/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.dropper

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
import java.awt.Color
import java.awt.Graphics2D

@Plugin(
    value = "dropper",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A colour picker
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@core#1.0.0"
    ]
)
object DropperPlugin : Tool(
    name = "Dropper",
    icon = MonoIcon.COLOUR_PICKER,
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
            var old: Color? = null

            override fun perform() {
                old = RawkyPlugin.colour
                RawkyPlugin.colour = cell.colour
            }

            override fun cleanup() {
                old?.let { RawkyPlugin.colour = it }
            }

            override fun outline(g2D: Graphics2D) {
            }
        }

        ActionStack.push(action)
    }
}
