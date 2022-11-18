/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.template.Template
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class NewFileDialog : TaskDialog(PluginUtil.window, "New File") {
    val template = JComboBox<Template>().apply {
        for ((_, v) in Template.registry) {
            addItem(v)
        }

        selectedIndex = -1

        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    with(this.selectedItem as Template) {
                        columnInput.value = this.width
                        rowInput.value = this.height
                    }
                }
            }
        }
    }

    val columnInput = JSpinner(SpinnerNumberModel(16, 1, 512, 8))
    val rowInput = JSpinner(SpinnerNumberModel(16, 1, 512, 8))
    private val sizeSwapper = JButton(MonoIcon.SWAP).apply {
        addActionListener {
            val temp = columnInput.value
            columnInput.value = rowInput.value
            rowInput.value = temp
        }
    }

    val framesInput = JSpinner(SpinnerNumberModel(1, 1, 1000, 1))
    val layersInput = JSpinner(SpinnerNumberModel(1, 1, 1000, 1))

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Template:"), StickEast)
            add(template, FillHorizontalFinishLine)

            add(JLabel("Size:"), StickEast)
            add(columnInput, FillHorizontal)
            add(JLabel("X"))
            add(rowInput, FillHorizontal)
            add(sizeSwapper, FinishLine)

            add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            add(JLabel("Initial Frames:"), StickEast)
            add(framesInput, FillHorizontalFinishLine)
            add(JLabel("Initial Layers:"), StickEast)
            add(layersInput, FillHorizontalFinishLine)
        }
    }
}
