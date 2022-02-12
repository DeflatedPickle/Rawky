@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.undulation.functions.extensions.toColour
import java.awt.Color
import javax.tools.Tool

@Plugin(
    value = "core",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        The core program
        <br>
        This provides the main API for Rawky
    """,
    type = PluginType.CORE_API,
    settings = RawkySettings::class,
)
@Suppress("unused")
object RawkyPlugin {
    var document: RawkyDocument? = null
    var colour: Colour = Color.CYAN.toColour()
        set(value) {
            field = value
            EventChangeColour.trigger(value)
        }
}