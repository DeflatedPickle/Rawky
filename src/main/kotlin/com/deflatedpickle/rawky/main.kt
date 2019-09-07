package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.utils.Commands
import com.deflatedpickle.rawky.utils.Components
import com.deflatedpickle.rawky.utils.Icons
import java.awt.BorderLayout
import javax.swing.*

fun main() {

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        // UIManager.setLookAndFeel(SubstanceGraphiteElectricLookAndFeel())
        Components.frame.layout = BorderLayout()

        Components.frame.add(JToolBar().apply {
            add(JButton(Icons.picture).apply {
                addActionListener {
                    Commands.save()
                }
            })
        }, BorderLayout.PAGE_START)

        val cControl = CControl(Components.frame)
        cControl.contentArea.isOpaque = false
        Components.frame.add(cControl.contentArea)

        val grid = CGrid(cControl)

        val toolbox = DefaultSingleCDockable("toolbox", "Toolbox", Components.toolbox)
        cControl.addDockable(toolbox)
        toolbox.isVisible = true
        grid.add(0.0, 0.0, 2.0, 0.3, toolbox)

        val tiledView = DefaultSingleCDockable("tiledView", "Tiled View", Components.tiledView)
        cControl.addDockable(tiledView)
        tiledView.isVisible = true
        grid.add(0.0, 0.3, 0.6, 1.0, tiledView)

        val layerList = DefaultSingleCDockable("layerList", "Layer List", Components.layerList)
        cControl.addDockable(layerList)
        layerList.isVisible = true
        grid.add(0.0, 1.0, 0.6, 1.0, layerList)

        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(Components.pixelGrid)

            add(JToolBar().apply {
                val slider = JSlider(1, 100).apply {
                    this.value = Components.pixelGrid.pixelSize
                    addChangeListener {
                        Components.pixelGrid.pixelSize = this.value
                        Components.pixelGrid.rectangleMatrix = Components.pixelGrid.zoom()
                    }
                }
                add(JButton(Icons.minus).apply {
                    addActionListener {
                        slider.value--
                    }
                })
                add(slider)
                add(JButton(Icons.plus).apply {
                    addActionListener {
                        slider.value++
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        grid.add(0.6, 0.3, 0.6, 2.0, pixelGrid)

        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", Components.colourPicker)
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.3, 0.4, 0.8, colourPicker)

        val colourShades = DefaultSingleCDockable("colourShades", "Colour Shades", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(Components.colourShades)

            add(JToolBar().apply {
                add(JSlider(3, 21).apply {
                    addChangeListener {
                        Components.colourShades.amount = this.value
                        Components.colourShades.createShades()
                        Components.colourShades.updateShades()
                        Components.colourShades.repaint()
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(colourShades)
        colourShades.isVisible = true
        grid.add(1.0, 0.8, 0.4, 0.4, colourShades)

        val colourPalette = DefaultSingleCDockable("colourPalette", "Colour Palette", Components.colourPalette)
        cControl.addDockable(colourPalette)
        colourPalette.isVisible = true
        grid.add(1.0, 1.2, 0.4, 0.6, colourPalette)

        val colourLibrary = DefaultSingleCDockable("colourLibrary", "Colour Library", Components.colourLibrary)
        cControl.addDockable(colourLibrary)
        colourLibrary.isVisible = true
        grid.add(1.0, 1.2, 0.4, 0.6, colourLibrary)

        cControl.contentArea.deploy(grid)

        Timer(1000 / 60) {
            Components.pixelGrid.repaint()
            Components.tiledView.repaint()
            Components.colourPalette.repaint()
            Components.layerList.repaint()
        }.start()
    }

    Components.frame.isVisible = true
}