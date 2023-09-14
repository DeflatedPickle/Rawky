/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.impex.imageio.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.widget.SliderSpinner
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.imageio.ImageWriteParam
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

// TODO: add a frame & layer input
class ExportImageDialog(
    param: ImageWriteParam
) : TaskDialog(Haruhi.window, "Export Image") {
    val compressionType = JComboBox<String>()
    private val compressionQualityCombo: JComboBox<String> = JComboBox<String>().apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    compressionQualitySlider.value = param.compressionQuality
                }
            }
        }
    }
    val compressionQualitySlider = SliderSpinner(
        0f,
        0f,
        1f,
    ).apply {
        addChangeListener {
            compressionQualityCombo.selectedItem = null
        }
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        if (param.canWriteCompressed()) {
            for (i in param.compressionTypes) {
                compressionType.addItem(i)
            }

            if (param.compressionTypes.isNotEmpty()
                && param.compressionType != null
                && param.compressionQualityDescriptions != null) {
                for (i in param.compressionQualityDescriptions) {
                    compressionQualityCombo.addItem(i)
                }

                compressionQualityCombo.addItemListener {
                    when (it.stateChange) {
                        ItemEvent.SELECTED ->
                            compressionQualitySlider.value = param.compressionQualityValues[compressionQualityCombo.selectedIndex]
                    }
                }
            }
        }

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                if (param.canWriteCompressed()) {
                    add(JLabel("Compression Type:"), StickEast)
                    add(compressionType, FillHorizontalFinishLine)
                    add(JLabel("Compression Quality:"), StickEast)
                    add(compressionQualityCombo, FillHorizontalFinishLine)
                    add(compressionQualitySlider, FillHorizontalFinishLine)
                }
            }
    }
}
