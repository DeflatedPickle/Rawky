@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.bucket.fill.solid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.api.Tool.Companion
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.tool.bucket.BucketPlugin
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.tool.bucket.api.Fill.Companion.registry
import java.awt.Color

@Plugin(
    value = "solid",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a solid fill for the bucket
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@bucket#*",
    ],
)
object SolidFillPlugin : Fill {
    override val name = "Solid"

    init {
        registry["deflatedpickle@${name}"] = this
    }

    override fun perform(cell: Cell, row: Int, column: Int, shade: Color) {
        cell.colour = shade
    }
}