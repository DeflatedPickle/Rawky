package com.deflatedpickle.rawky.util

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.rawky.Core
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
            val maxRows = NewFileDialog.rowInput.text.toInt()
            val maxColumns = NewFileDialog.columnInput.text.toInt()

            val document = newDocument(maxRows, maxColumns)

            Core.document = document

            EventCreateDocument.trigger(document)
        }
    }

    fun newDocument(rows: Int, columns: Int): RawkyDocument = RawkyDocument(
        children = Array(NewFileDialog.framesInput.text.toInt()) {
            Frame(
                Array(NewFileDialog.layersInput.text.toInt()) {
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