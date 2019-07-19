package com.deflatedpickle.rawky

import bibliothek.gui.DockTheme
import bibliothek.gui.dock.common.CControl
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

        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", PixelGrid())
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        pixelGrid.setLocation(CLocation.base().normal())
    }

    frame.isVisible = true
}