package com.deflatedpickle.rawky

import bibliothek.gui.DockTheme
import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.CLocation
import bibliothek.gui.dock.common.DefaultSingleCDockable
import bibliothek.gui.dock.util.BackgroundPaint
import com.deflatedpickle.rawky.components.PixelGrid
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager
import bibliothek.gui.dock.util.BackgroundComponent
import java.awt.Graphics
import bibliothek.gui.dock.util.PaintableComponent
import bibliothek.gui.dock.util.Transparency
import com.bric.colorpicker.ColorPicker
import com.deflatedpickle.rawky.components.Components
import javax.swing.JButton


fun main() {
    val frame = JFrame("Rawky")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(400, 400)

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
        grid.add(0.0, 0.0, 1.0, 1.0, pixelGrid)

        Components.colourPicker = ColorPicker(false, true)
        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", Components.colourPicker)
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.0, 1.0, 1.0, colourPicker)

        cControl.contentArea.deploy(grid)
    }

    frame.isVisible = true
}