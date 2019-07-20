package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.components.PixelGrid
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager
import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.components.ColourShades
import com.deflatedpickle.rawky.components.Components


fun main() {
    val frame = JFrame("Rawky")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(600, 400)

    SwingUtilities.invokeLater {
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        UIManager.setLookAndFeel(SubstanceGraphiteElectricLookAndFeel())

        val cControl = CControl(frame)
        cControl.contentArea.isOpaque = false
        frame.add(cControl.contentArea)

        val grid = CGrid(cControl)

        Components.pixelGrid = PixelGrid()
        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", Components.pixelGrid)
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        grid.add(0.0, 0.0, 1.0, 2.0, pixelGrid)

        Components.colourPicker = ColorPicker(false, true)
        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", Components.colourPicker)
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.0, 0.4, 1.0, colourPicker)

        Components.colourShades = ColourShades()
        val colourShades = DefaultSingleCDockable("colourShades", "Colour Shades", Components.colourShades)
        cControl.addDockable(colourShades)
        colourShades.isVisible = true
        grid.add(1.0, 1.0, 0.4, 0.2, colourShades)

        cControl.contentArea.deploy(grid)
    }

    frame.isVisible = true
}