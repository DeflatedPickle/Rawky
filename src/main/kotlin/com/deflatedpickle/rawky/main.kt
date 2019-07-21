package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.components.Components
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import javax.swing.JFrame
import javax.swing.JToolBar
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    val frame = JFrame("Rawky")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(700, 500)

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        // UIManager.setLookAndFeel(SubstanceGraphiteElectricLookAndFeel())

        val cControl = CControl(frame)
        cControl.contentArea.isOpaque = false
        frame.add(cControl.contentArea)

        val grid = CGrid(cControl)

        val toolbox = DefaultSingleCDockable("toolbox", "Toolbox", Components.toolbox)
        cControl.addDockable(toolbox)
        toolbox.isVisible = true
        grid.add(0.0, 0.0, 0.2, 2.0, toolbox)

        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", Components.pixelGrid)
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        grid.add(0.2, 0.0, 0.6, 2.0, pixelGrid)

        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", Components.colourPicker)
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.0, 0.4, 1.0, colourPicker)

        val colourShades = DefaultSingleCDockable("colourShades", "Colour Shades", Components.colourShades)
        cControl.addDockable(colourShades)
        colourShades.isVisible = true
        grid.add(1.0, 0.4, 0.4, 0.6, colourShades)

        val colourPalette = DefaultSingleCDockable("colourPalette", "Colour Palette", Components.colourPalette)
        cControl.addDockable(colourPalette)
        colourPalette.isVisible = true
        grid.add(1.0, 1.0, 0.4, 0.6, colourPalette)

        cControl.contentArea.deploy(grid)
    }

    frame.isVisible = true
}