/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.ResampleCollection
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class ScaleImageDialog(scale: String) : TaskDialog(Haruhi.window, "Scale $scale") {
    val columnInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    val rowInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    private val sizeSwapper =
        JButton(MonoIcon.SWAP).apply {
            addActionListener {
                val temp = columnInput.value
                columnInput.value = rowInput.value
                rowInput.value = temp
            }
        }

    val resamplerComboBox = JComboBox<ResampleCollection.Resampler>().apply {
        for (i in ResampleCollection.current.resamplers) {
            addItem(i)
        }
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JLabel("Size:"), StickEast)
                add(columnInput, FillHorizontal)
                add(JLabel("X"))
                add(rowInput, FillHorizontal)
                add(sizeSwapper, FinishLine)

                add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

                add(JLabel("Resampler:"), StickEast)
                add(resamplerComboBox, FillHorizontalFinishLine)
            }
    }
}
