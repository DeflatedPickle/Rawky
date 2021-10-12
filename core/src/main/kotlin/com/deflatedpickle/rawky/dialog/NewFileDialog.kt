package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel

object NewFileDialog : TaskDialog(PluginUtil.window, "New File") {
    val rowInput = JSpinner(SpinnerNumberModel(16, 1, 512, 8))
    val columnInput = JSpinner(SpinnerNumberModel(16, 1, 512, 8))

    val framesInput = JSpinner(SpinnerNumberModel(1, 1, 1000, 1))
    val layersInput = JSpinner(SpinnerNumberModel(1, 1, 1000, 1))

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Size:"), StickEast)
            add(rowInput, FillHorizontal)
            add(JLabel("X"))
            add(columnInput, FillHorizontalFinishLine)

            add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            add(JLabel("Initial Frames:"), StickEast)
            add(framesInput, FillHorizontalFinishLine)
            add(JLabel("Initial Layers:"), StickEast)
            add(layersInput, FillHorizontalFinishLine)
        }
    }
}