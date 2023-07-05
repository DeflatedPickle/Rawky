/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class NewFrameDialog : TaskDialog(Haruhi.window, "New Frame") {
    val nameInput = JXTextField("Name").apply {
        RawkyPlugin.document?.let { doc ->
            text = "Frame ${doc.children.size}"
        }
    }
    val indexInput = JSpinner(SpinnerNumberModel(0, 0, null, 1)).apply {
        RawkyPlugin.document?.let { doc ->
            value = doc.children.size
        }
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Name:"), StickEast)
            add(nameInput, FillHorizontalFinishLine)

            add(JLabel("Index:"), StickEast)
            add(indexInput, FillHorizontalFinishLine)
        }
    }
}
