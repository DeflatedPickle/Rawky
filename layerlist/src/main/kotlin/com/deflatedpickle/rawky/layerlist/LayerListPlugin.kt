/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.layerlist

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.util.ComponentPosition.WEST
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.extension.removeAll
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively

@Plugin(
    value = "layer_list",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a table to manipulate layers
    """,
    type = PluginType.COMPONENT,
    component = LayerListPanel::class,
    dependencies = [
        "deflatedpickle@core#*",
    ],
    componentMinimizedPosition = WEST,
    componentVisible = false,
)
object LayerListPlugin {
    init {
        EventChangeTheme.addListener {
            LayerListPanel.updateUIRecursively()
        }

        EventCreateDocument.addListener {
            LayerListPanel.model.removeAll()
            createInitialLayers(it as RawkyDocument)
            LayerListPanel.table.setRowSelectionInterval(0, 0)
        }

        EventOpenDocument.addListener {
            LayerListPanel.model.removeAll()
            createInitialLayers(it.first as RawkyDocument)
            LayerListPanel.table.setRowSelectionInterval(0, 0)
        }

        EventChangeFrame.addListener {
            LayerListPanel.model.removeAll()

            for (i in it.new.children) {
                LayerListPanel.model.insertRow(
                    0,
                    arrayOf(
                        null,
                        i.name,
                        i.visible,
                        i.lock
                    )
                )
            }
        }

        EventCreateDocument.addListener {
            for (i in LayerListPanel.toolbar.components) {
                i.isEnabled = true
            }

            for (i in LayerListPanel.navbar.components) {
                i.isEnabled = true
            }
        }
    }

    private fun createInitialLayers(it: RawkyDocument) {
        for (i in it.children[0].children) {
            LayerListPanel.model.insertRow(
                0,
                arrayOf(
                    null,
                    i.name,
                    i.visible,
                    i.lock
                )
            )
        }
    }
}
