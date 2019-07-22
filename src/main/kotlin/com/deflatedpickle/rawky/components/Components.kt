package com.deflatedpickle.rawky.components

import com.bric.colorpicker.ColorPicker
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import java.awt.Color
import java.awt.event.ActionEvent

object Components {
    val toolbox = Toolbox()
    val pixelGrid = PixelGrid()
    val colourPicker = ColorPicker(false, true)
    val colourShades = ColourShades()
    val colourPalette = ColourPalette()

    init {
        toolbox.pencilButton.isSelected = true

        colourPicker.color = Color.WHITE
        colourPicker.addColorListener {
            colourShades.colour = colourPicker.color

            val shades = colourShades.getShades()
            for ((index, button) in colourShades.buttonList.withIndex()) {
                button.backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[index]))
            }
            colourShades.selectedButton.actionListeners[0].actionPerformed(ActionEvent(colourShades.selectedButton, 0, ""))
            colourShades.selectedButton.text = " "
        }
    }
}