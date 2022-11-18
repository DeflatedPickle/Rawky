package com.deflatedpickle.rawky.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.template.Template
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel

class NewFrameDialog : TaskDialog(PluginUtil.window, "New Frame") {
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