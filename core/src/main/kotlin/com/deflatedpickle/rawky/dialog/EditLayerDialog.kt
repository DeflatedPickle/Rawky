/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.widget.SliderSpinner
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class EditLayerDialog(val index: Int = -1) : TaskDialog(Haruhi.window, "Edit Layer") {
    val nameInput =
        JXTextField("Name").apply {
            text =
                RawkyPlugin.document?.let { doc ->
                    val frame = doc.children[doc.selectedIndex]
                    frame.children[frame.selectedIndex].name
                }
        }

    val opacitySlider = SliderSpinner(1f, 0f, 1f)

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent =
            JPanel().apply {
                layout = GridBagLayout()

                add(JLabel("Name:"), StickEast)
                add(nameInput, FillHorizontalFinishLine)

                add(JLabel("Opacity:"), StickEast)
                add(opacitySlider, FillHorizontalFinishLine)
            }
    }
}
