/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.template.Guide
import com.deflatedpickle.rawky.api.template.Guide.Companion
import com.deflatedpickle.rawky.api.template.Template
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

            val document = newDocument(maxRows, maxColumns, frames, layers)
            (dialog.template.selectedItem as Template?)?.guides
                ?.flatMap { Guide.registry[it]!! }?.let { document.guides = it }

            RawkyPlugin.document = document

            EventCreateDocument.trigger(document)
        }
    }

    fun newDocument(columns: Int, rows: Int, frames: Int, layers: Int): RawkyDocument = RawkyDocument(
        children = Array(frames) {
            Frame(
                name = "Frame 0",
                children = Array(layers) {
                    Layer(
                        name = "Layer 0",
                        child = Grid(
                            rows = rows,
                            columns = columns
                        )
                    )
                }.toMutableList()
            )
        }.toMutableList()
    )
}
