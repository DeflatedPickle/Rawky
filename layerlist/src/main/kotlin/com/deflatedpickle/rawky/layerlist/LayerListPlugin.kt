/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.layerlist

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventChangeFrame
import com.deflatedpickle.rawky.event.EventChangeLayer
import com.deflatedpickle.rawky.event.EventNewLayer
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
    dependencies =
    [
        "deflatedpickle@core#*",
    ],
)
object LayerListPlugin {
    init {
        EventChangeTheme.addListener { LayerListPanel.updateUIRecursively() }

        EventCreateDocument.addListener {
            LayerListPanel.model.removeAll()
            createInitialLayers(it as RawkyDocument)

            val row = LayerListPanel.table.rowCount - 1
            LayerListPanel.table.setRowSelectionInterval(row, row)

            triggerButtons()
        }

        EventOpenDocument.addListener {
            LayerListPanel.model.removeAll()
            createInitialLayers(it.first as RawkyDocument)

            val row = LayerListPanel.table.rowCount - 1
            LayerListPanel.table.setRowSelectionInterval(row, row)

            triggerButtons()
        }

        EventChangeFrame.addListener {
            LayerListPanel.model.removeAll()

            for (i in it.new.children) {
                LayerListPanel.model.addRow(arrayOf(i, i.name, i.visible, i.lock))
            }

            triggerButtons()
        }

        EventChangeLayer.addListener { triggerButtons() }

        EventNewLayer.addListener {
            LayerListPanel.model.addRow(arrayOf(it, it.name, true, false))

            val row = LayerListPanel.table.rowCount - 1
            LayerListPanel.table.setRowSelectionInterval(row, row)
        }
    }

    private fun createInitialLayers(it: RawkyDocument) {
        for (i in it.children[0].children) {
            LayerListPanel.model.addRow(arrayOf(i, i.name, i.visible, i.lock))
        }
    }

    private fun triggerButtons() {
        LayerListPanel.addButton.isEnabled = true
        LayerListPanel.editButton.isEnabled = true

        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]

            LayerListPanel.deleteButton.isEnabled = frame.children.size > 1
        }

        for (i in LayerListPanel.navbar.components) {
            i.isEnabled = true
        }
    }

    fun triggerNavButtons() {
        RawkyPlugin.document?.let { doc ->
            val frame = doc[doc.selectedIndex]
            val index = LayerListPanel.table.selectedRow

            LayerListPanel.firstButton.isEnabled = index - 1 >= 0
            LayerListPanel.decrementButton.isEnabled = index - 1 >= 0
            LayerListPanel.incrementButton.isEnabled = index + 1 < frame.children.size
            LayerListPanel.lastButton.isEnabled = index + 1 < frame.children.size
        }
    }
}
