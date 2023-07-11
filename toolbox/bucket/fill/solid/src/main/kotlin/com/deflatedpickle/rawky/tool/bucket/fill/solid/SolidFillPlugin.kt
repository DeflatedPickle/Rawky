/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.bucket.fill.solid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.tool.bucket.api.Fill.Companion.registry

@Plugin(
    value = "solid",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a solid fill for the bucket
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@bucket#*",
    ],
)
object SolidFillPlugin : Fill {
    override val name = "Solid"

    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(cell: Cell<Any>, row: Int, column: Int) {
        CellProvider.current.perform(cell, 0, false, 1)
    }
}
