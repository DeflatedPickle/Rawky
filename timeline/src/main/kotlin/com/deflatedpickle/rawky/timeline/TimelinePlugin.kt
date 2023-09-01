/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.timeline

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventNewFrame
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
    dependencies =
    [
        "deflatedpickle@core#*",
    ],
)
object TimelinePlugin {
    init {
        EventChangeTheme.addListener { TimelinePanel.updateUIRecursively() }

        EventCreateDocument.addListener {
            TimelinePanel.model.removeAllElements()
            createInitialFrames(it as RawkyDocument)
            TimelinePanel.list.selectedIndex = 0

            triggerButtons()
            triggerNavButtons()
        }

        EventOpenDocument.addListener {
            TimelinePanel.model.removeAllElements()
            createInitialFrames(it.first as RawkyDocument)
            TimelinePanel.list.selectedIndex = 0

            triggerButtons()
            triggerNavButtons()
        }

        EventChangeFrame.addListener {
            triggerButtons()
            triggerNavButtons()
        }

        EventNewFrame.addListener {
            TimelinePanel.model.addElement(it)
        }
    }

    private fun createInitialFrames(it: RawkyDocument) {
        for (i in it.children) {
            TimelinePanel.model.addElement(i)
        }
    }

    private fun triggerButtons() {
        TimelinePanel.addButton.isEnabled = true
        TimelinePanel.editButton.isEnabled = true

        RawkyPlugin.document?.let { doc ->
            TimelinePanel.deleteButton.isEnabled = doc.children.size > 1
        }

        for (i in TimelinePanel.navbar.components) {
            i.isEnabled = true
        }
    }

    fun triggerNavButtons() {
        RawkyPlugin.document?.let { doc ->
            val index = TimelinePanel.list.selectedIndex

            TimelinePanel.firstButton.isEnabled = index - 1 >= 0
            TimelinePanel.decrementButton.isEnabled = index - 1 >= 0
            TimelinePanel.incrementButton.isEnabled = index + 1 < doc.children.size
            TimelinePanel.lastButton.isEnabled = index + 1 < doc.children.size
        }
    }
}
