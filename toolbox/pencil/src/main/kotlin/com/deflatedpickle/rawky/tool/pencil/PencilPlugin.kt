/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.pencil

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
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
    dependencies = ["deflatedpickle@core#1.0.0"],
    settings = PencilSettings::class,
)
object PencilPlugin :
    Tool<PencilSettings>(
        name = "Pencil",
        icon = MonoIcon.PENCIL,
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
                    ConfigUtil.getSettings<PencilSettings>("deflatedpickle@pencil#")?.let {

                        for (column in 0 downTo -it.size + 1) {
                            for (row in 0 downTo -it.size + 1) {
                                try {
                                    CellProvider.current.perform(
                                        cell.grid[cell.row + row, cell.column + column],
                                        button,
                                        dragged,
                                        clickCount,
                                    )
                                } catch (_: IndexOutOfBoundsException) {}
                            }
                        }
                    }
                }

                override fun cleanup() {
                    // TODO
                }

                override fun outline(g2D: Graphics2D) {}
            }

        ActionStack.push(action)
    }

    override fun getSettings(): PencilSettings? = ConfigUtil.getSettings("deflatedpickle@pencil#*")
    override fun getQuickSettings() = listOf("size")
}
