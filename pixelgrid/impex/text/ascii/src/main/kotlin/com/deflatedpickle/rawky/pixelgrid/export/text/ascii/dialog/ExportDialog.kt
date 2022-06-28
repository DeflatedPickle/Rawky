package com.deflatedpickle.rawky.pixelgrid.export.text.ascii.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.pixelgrid.export.text.ascii.api.Palette
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class ExportDialog : TaskDialog(PluginUtil.window, "Export") {
    val paletteCombo = JComboBox(Palette.registry.values.toTypedArray())

    val frameSpinner = JSpinner(
        SpinnerNumberModel(
            0,
            0,
            RawkyPlugin.document?.children?.size?.minus(1),
            1
        )
    )
    val openCheck = JCheckBox("Open?")

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Palette:"), StickEast)
            add(paletteCombo, FillHorizontalFinishLine)

            add(JSeparator(JSeparator.HORIZONTAL), FillHorizontalFinishLine)

            add(frameSpinner, FillHorizontalFinishLine)
            add(openCheck, StickEast)
        }
    }
}