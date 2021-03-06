/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.ToolOptions
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.Slider
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import org.oxbow.swingbits.dialog.task.TaskDialog

class New : TaskDialog(Components.frame, "New File") {
    val widthSpinner = Slider.IntSliderComponent(0, 16 * 16, PixelGrid.columnAmount).apply {
        this.slider.value = PixelGrid.columnAmount
    }
    val heightSpinner = Slider.IntSliderComponent(0, 16 * 16, PixelGrid.rowAmount).apply {
        this.slider.value = PixelGrid.rowAmount
    }

    private val result = object : Property<Command>("imageResult", true) {
        private var value: Command = StandardCommand.OK

        override fun get(): Command = value
        override fun setValue(command: Command) {
            value = command
        }
    }

    init {
        for (i in setOf(widthSpinner, heightSpinner)) {
            i.slider.majorTickSpacing = 32
            i.slider.minorTickSpacing = 16
            i.slider.snapToTicks = true

            i.slider.paintTicks = true
            i.slider.paintLabels = true

            i.isOpaque = false
            i.slider.isOpaque = false
        }

        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        fixedComponent = JPanel().apply {
            layout = GridBagLayout()
            isOpaque = false

            add(JLabel("Width:"), ToolOptions.StickEast)
            add(widthSpinner, ToolOptions.FinishLine)

            add(JLabel("Height:"), ToolOptions.StickEast)
            add(heightSpinner, ToolOptions.FinishLine)
        }
    }

    override fun getResult(): Command = result.get()
}
