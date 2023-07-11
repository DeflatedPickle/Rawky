/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourwheel.component

import com.bric.colorpicker.ColorPickerDialog
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.undulation.widget.ColourButton
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.event.ChangeEvent

class DoubleClickButton(colour: Color, action: (DoubleClickButton) -> Unit) : ColourButton(colour) {
    class Adapter(private val button: DoubleClickButton) : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2 && e.button == MouseEvent.BUTTON1) {
                button.colour = ColorPickerDialog.showDialog(Haruhi.window, button.colour, true)
                button.colour = button.colour
                button.changeListeners.forEach { it.stateChanged(ChangeEvent(button)) }
            }
        }
    }

    init {
        isBorderPainted = true

        addActionListener { action(this) }

        addMouseListener(Adapter(this))
    }
}
