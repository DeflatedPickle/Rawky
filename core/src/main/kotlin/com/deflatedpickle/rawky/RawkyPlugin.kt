@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventSerializeConfig
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.Colour
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.undulation.functions.extensions.toColour
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Color

@OptIn(ExperimentalSerializationApi::class)
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

    init {
        EventChangeTool.addListener {
            PluginUtil.window.cursor = it.asCursor()
        }

        EventSerializeConfig.addListener {
            if ("core" in it.name) {
                if (Tool.isToolValid()) {
                    EventChangeTool.trigger(Tool.current)
                }
            }
        }
    }
}