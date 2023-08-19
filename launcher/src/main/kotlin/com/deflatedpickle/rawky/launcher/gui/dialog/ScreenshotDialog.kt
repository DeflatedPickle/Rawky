/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.rawky.launcher.api.ScreenShotArea
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.constraints.StickEastFinishLine
import com.deflatedpickle.undulation.constraints.StickWestFinishLine
import com.deflatedpickle.undulation.widget.SliderSpinner
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTitledPanel
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator

class ScreenshotDialog : TaskDialog(Haruhi.window, "Take Screenshot") {
    private val areaOptions = JXTitledPanel("Area Options").apply {
        contentContainer = JXPanel(GridBagLayout())
    }
    val areaComboBox = JComboBox(ScreenShotArea.values()).apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    areaOptions.contentContainer.removeAll()

                    when (it.item) {
                        ScreenShotArea.PROGRAM -> {
                            areaOptions.contentContainer.apply {
                                add(includeDecoration, FillHorizontalFinishLine)
                            }
                        }
                    }

                    areaOptions.contentContainer.revalidate()
                    areaOptions.contentContainer.repaint()
                }
            }
        }
    }

    val includeDecoration = JCheckBox("Include Window Decoration")

    val delaySpinner = SliderSpinner(0, 0, 100)

    val openCheckBox = JCheckBox("Open")

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        areaComboBox.itemListeners[0].itemStateChanged(
            ItemEvent(
                areaComboBox, ItemEvent.ITEM_STATE_CHANGED,
                ScreenShotArea.PROGRAM,
                ItemEvent.SELECTED
            )
        )

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JLabel("Area:"), StickEast)
                add(areaComboBox, FillHorizontalFinishLine)

                add(areaOptions, FillBothFinishLine)

                add(JLabel("Delay:"), StickEast)
                add(delaySpinner, FillHorizontalFinishLine)

                add(openCheckBox, StickEastFinishLine)
            }
    }
}
