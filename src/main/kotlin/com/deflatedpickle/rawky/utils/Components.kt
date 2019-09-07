package com.deflatedpickle.rawky.utils

import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.components.*
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import java.awt.Color
import java.awt.event.ActionEvent
import javax.swing.JFrame

object Components {
    val frame = JFrame("Rawky")

    val toolbox = Toolbox()
    val pixelGrid = PixelGrid()
    val tiledView = TiledView()
    val colourPicker = ColorPicker(false, true)
    val colourShades = ColourShades()
    val colourLibrary = ColourLibrary()
    val colourPalette = ColourPalette()
    val layerList = LayerList()

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(900, 600)

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

        layerList.addLayer()
    }
}