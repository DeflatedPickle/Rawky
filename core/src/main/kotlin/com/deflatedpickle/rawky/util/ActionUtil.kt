package com.deflatedpickle.rawky.util

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.dialog.NewFileDialog
import org.oxbow.swingbits.dialog.task.TaskDialog

object ActionUtil {
    fun newFile() {
        NewFileDialog.isVisible = true

        if (NewFileDialog.result == TaskDialog.StandardCommand.OK) {
            val maxRows = NewFileDialog.rowInput.value as Int
            val maxColumns = NewFileDialog.columnInput.value as Int
            val frames = NewFileDialog.framesInput.value as Int
            val layers = NewFileDialog.layersInput.value as Int

            val document = newDocument(maxRows, maxColumns, frames, layers)

            RawkyPlugin.document = document

            EventCreateDocument.trigger(document)
        }
    }

    fun newDocument(rows: Int, columns: Int, frames: Int, layers: Int): RawkyDocument = RawkyDocument(
        children = Array(frames) {
            Frame(
                Array(layers) {
                    Layer(
                        Grid(
                            rows = rows,
                            columns = columns
                        )
                    )
                }
            )
        }
    )
}