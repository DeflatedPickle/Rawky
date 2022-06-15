package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventSerializeConfig
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.event.EventChangeTool
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.event.EventUpdateGrid
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        Provides a grid to draw upon
        <br>
        This plugin provides most of the basic user functionality of Rawky.
    """,
    type = PluginType.COMPONENT,
    component = PixelGridPanel::class,
    dependencies = [
        "deflatedpickle@core#*",
        "deflatedpickle@discord_rpc#*"
    ],
    settings = PixelGridSettings::class
)
@Suppress("unused")
object PixelGridPlugin {
    init {
        EventCreateDocument.addListener {
            PixelGridPanel.repaint()
        }

        EventUpdateGrid.addListener {
            PixelGridPanel.repaint()
        }

        EventChangeTool.addListener {
            PluginUtil.window.cursor = it.asCursor()
        }

        EventSerializeConfig.addListener {
            if ("core" in it.name && Tool.isToolValid()) {
                EventChangeTool.trigger(Tool.current)
            }
        }
    }
}