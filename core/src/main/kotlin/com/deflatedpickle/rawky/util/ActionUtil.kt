/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.util

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.ColourChannel
import com.deflatedpickle.rawky.api.ControlMode
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.api.template.Template
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.dialog.NewFileDialog
import com.deflatedpickle.rawky.setting.RawkyDocument
import org.oxbow.swingbits.dialog.task.TaskDialog

object ActionUtil {
    fun newFile() {
        val dialog = NewFileDialog()
        dialog.isVisible = true

        if (dialog.result == TaskDialog.StandardCommand.OK) {
            val maxRows = dialog.rowInput.value as Int
            val maxColumns = dialog.columnInput.value as Int
            val frames = dialog.framesInput.value as Int
            val layers = dialog.layersInput.value as Int

            ControlMode.current = dialog.controlModeComboBox.selectedItem as ControlMode
            CellProvider.current = dialog.cellProviderComboBox.selectedItem as CellProvider<Cell<Any>>

            val document = newDocument(maxRows, maxColumns, ColourChannel.ARGB, frames, layers).apply {
                RawkyDocument.suggestedName = null
                RawkyDocument.suggestedExtension = null

                if (dialog.nameInput.text.isNotEmpty()) {
                    val name = dialog.nameInput.text
                    if (name.isNotBlank()) {
                        RawkyDocument.suggestedName = name
                    }

                    val ext = dialog.extensionComboBox.selectedItem
                    if (ext != null) {
                        RawkyDocument.suggestedExtension = ext as String
                    }
                }

                controlMode = ControlMode.current
                cellProvider = CellProvider.current
            }

            (dialog.template.selectedItem as Template?)
                ?.guides
                ?.flatMap { Guide.registry[it]!! }
                ?.let { document.guides = it }

            document.controlMode = ControlMode.current
            document.cellProvider = CellProvider.current

            RawkyPlugin.document = document
            ControlMode.current.apply()

            EventCreateDocument.trigger(document)
        }
    }

    fun newDocument(rows: Int, columns: Int, colourChannel: ColourChannel, frames: Int, layers: Int): RawkyDocument =
        RawkyDocument(
            rows = rows,
            columns = columns,
            colourChannel = colourChannel,
            children =
            Array(frames) { f ->
                Frame(
                    name = "Frame $f",
                    children =
                    Array(layers) { l ->
                        Layer(
                            name = "Layer $l",
                            child = Grid(rows = rows, columns = columns),
                        )
                    }.toMutableList(),
                )
            }.toMutableList(),
        )
}
