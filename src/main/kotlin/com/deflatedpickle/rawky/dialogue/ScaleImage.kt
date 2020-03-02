/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.ToolOptions
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.extension.toCamelCase
import com.deflatedpickle.rawky.widget.Slider
import java.awt.GridBagLayout
import java.awt.image.AffineTransformOp
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import org.oxbow.swingbits.dialog.task.TaskDialog

class ScaleImage : TaskDialog(Components.frame, "Scale Image") {
    enum class ScaleType(val type: Int) {
        NEAREST_NEIGHBOR(AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
        BILINEAR(AffineTransformOp.TYPE_BILINEAR),
        BICUBIC(AffineTransformOp.TYPE_BICUBIC),
    }

    val scaleCombobox = JComboBox<String>(
            ScaleType::class.java.enumConstants.map { e -> e.toString().toCamelCase() }
                    .toTypedArray()
    )

    val widthSlider = Slider.IntSliderComponent(0, 16 * 16, PixelGrid.columnAmount).apply {
        this.slider.value = PixelGrid.columnAmount
    }
    val heightSlider = Slider.IntSliderComponent(0, 16 * 16, PixelGrid.rowAmount).apply {
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
        for (i in setOf(widthSlider, heightSlider)) {
            i.slider.majorTickSpacing = 32
            i.slider.minorTickSpacing = 16
            i.slider.snapToTicks = true

            i.slider.paintTicks = true
            i.slider.paintLabels = true

            i.isOpaque = false
            i.slider.isOpaque = false
        }

        // TODO: Add a copy button so it doesn't have to be saved
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        fixedComponent = JPanel().apply {
            layout = GridBagLayout()
            isOpaque = false

            add(JLabel("Scale Type:"), ToolOptions.StickEast)
            add(scaleCombobox, ToolOptions.FillHorizontalFinishLine)

            add(JLabel("Width:"), ToolOptions.StickEast)
            add(widthSlider, ToolOptions.FillHorizontalFinishLine)

            add(JLabel("Height:"), ToolOptions.StickEast)
            add(heightSlider, ToolOptions.FillHorizontalFinishLine)
        }
    }

    override fun getResult(): Command = result.get()
}
