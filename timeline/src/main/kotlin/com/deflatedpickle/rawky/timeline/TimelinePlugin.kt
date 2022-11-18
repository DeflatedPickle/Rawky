/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.timeline

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition.SOUTH
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "timeline",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a list to manipulate frames
    """,
    type = PluginType.COMPONENT,
    component = TimelinePanel::class,
    dependencies = [
        "deflatedpickle@core#*",
    ],
    componentMinimizedPosition = SOUTH,
    componentVisible = false,
)
object TimelinePlugin {
    init {
        EventChangeTheme.addListener {
            TimelinePanel.updateUIRecursively()
        }

        EventCreateDocument.addListener {
            TimelinePanel.model.removeAllElements()
            createInitialFrames(it as RawkyDocument)
            TimelinePanel.list.selectedIndex = 0
        }

        EventOpenDocument.addListener {
            TimelinePanel.model.removeAllElements()
            createInitialFrames(it.first as RawkyDocument)
            TimelinePanel.list.selectedIndex = 0
        }
    }

    private fun createInitialFrames(it: RawkyDocument) {
        for (i in it.children) {
            TimelinePanel.model.addElement(i)
        }
    }
}
