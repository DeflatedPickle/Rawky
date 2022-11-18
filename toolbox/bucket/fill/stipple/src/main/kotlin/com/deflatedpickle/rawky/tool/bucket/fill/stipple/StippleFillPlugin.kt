/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.tool.bucket.fill.stipple

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.tool.bucket.api.Fill
import com.deflatedpickle.rawky.tool.bucket.api.Fill.Companion.registry
import java.awt.Color

@Plugin(
    value = "stipple",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a stipple fill for the bucket
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@bucket#*",
    ],
    settings = StippleSettings::class,
)
object StippleFillPlugin : Fill {
    override val name = "Stipple"

    init {
        registry["deflatedpickle@$name"] = this
    }

    override fun perform(cell: Cell, row: Int, column: Int, shade: Color) {
        ConfigUtil.getSettings<StippleSettings>("deflatedpickle@stipple#*")?.let {
            if (row % it.modulusRow == column % it.modulusColumn) {
                cell.colour = shade
            } else {
                if (it.altColour != RawkyPlugin.colour) {
                    cell.colour = it.altColour
                }
            }
        }
    }
}
