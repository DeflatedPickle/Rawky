/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.dropper

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.ActionStack.Action
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
    dependencies = ["deflatedpickle@core#1.0.0"],
)
object DropperPlugin :
    Tool<Nothing>(
        name = "Dropper",
        icon = MonoIcon.COLOUR_PICKER,
    ) {
    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    ) {
        val action =
            object : Action(name) {
                override fun perform() {
                    CellProvider.current.current(cell.content)
                    EventChangeColour.trigger(PixelCellPlugin.current)
                }

                override fun cleanup() {}

                override fun outline(g2D: Graphics2D) {}
            }

        ActionStack.push(action)
    }

    override fun getSettings() = null
}
