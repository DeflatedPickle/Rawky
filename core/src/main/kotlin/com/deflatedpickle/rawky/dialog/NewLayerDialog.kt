/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class NewLayerDialog(val index: Int = -1) : TaskDialog(Haruhi.window, "New Layer") {
    val nameInput =
        JXTextField("Name").apply {
            text =
                "Layer ${if (index == -1) {
                    RawkyPlugin.document?.let { doc ->
                        val frame = doc.children[doc.selectedIndex]
                        frame.children.size
                    }
                } else {
                    index
                }}"
        }

  /*val indexInput = JSpinner(SpinnerNumberModel(0, 0, null, 1)).apply {
      RawkyPlugin.document?.let { doc ->
          val frame = doc.children[doc.selectedIndex]
          value = frame.children.size
      }
  }*/
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

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JLabel("Name:"), StickEast)
                add(nameInput, FillHorizontalFinishLine)

          /*add(JLabel("Index:"), StickEast)
          add(indexInput, FillHorizontalFinishLine)*/

                add(JLabel("Size:"), StickEast)
                add(columnInput, FillHorizontal)
                add(JLabel("X"))
                add(rowInput, FillHorizontal)
                add(sizeSwapper, FinishLine)
            }
    }
}
