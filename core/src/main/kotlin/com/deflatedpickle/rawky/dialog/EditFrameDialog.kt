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

class EditFrameDialog(val index: Int = -1) : TaskDialog(Haruhi.window, "Edit Frame") {
    val nameInput =
        JXTextField("Name").apply {
            text = RawkyPlugin.document?.let { doc -> doc.children[doc.selectedIndex].name }
        }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent =
            JPanel().apply {
                layout = GridBagLayout()

                add(JLabel("Name:"), StickEast)
                add(nameInput, FillHorizontalFinishLine)
            }
    }
}
