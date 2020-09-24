package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JTextField

object NewFileDialog : TaskDialog(PluginUtil.window, "New File") {
    // Labels
    private val rowLabel = JLabel("Rows:")
    private val columnLabel = JLabel("Columns:")

    private val framesLabel = JLabel("Initial Frames:")
    private val layersLabel = JLabel("Initial Layers:")

    // Inputs
    val rowInput = JTextField("16")
    val columnInput = JTextField("16")

    val framesInput = JTextField("1")
    val layersInput = JTextField("1")

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            this.add(rowLabel, StickEast)
            this.add(rowInput, FillHorizontalFinishLine)
            this.add(columnLabel, StickEast)
            this.add(columnInput, FillHorizontalFinishLine)

            this.add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            this.add(framesLabel, StickEast)
            this.add(framesInput, FillHorizontalFinishLine)
            this.add(layersLabel, StickEast)
            this.add(layersInput, FillHorizontalFinishLine)
        }
    }
}