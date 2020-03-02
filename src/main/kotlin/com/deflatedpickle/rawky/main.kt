/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.api.component.ComponentFrame
import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.menu.Edit
import com.deflatedpickle.rawky.menu.File
import com.deflatedpickle.rawky.menu.Help
import com.deflatedpickle.rawky.menu.Program
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Components.cControl
import com.deflatedpickle.rawky.util.Icons
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JMenuBar
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JToolBar
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        Components.frame.jMenuBar = JMenuBar().apply {
            add(File())
            add(Edit())
            add(Program())
            add(Help())
        }

        Components.frame.layout = BorderLayout()

        Components.frame.add(JToolBar().apply {
            add(JButton(Icons.createNew).apply {
                toolTipText = "New File"
                addActionListener { Commands.newDialog() }
            })
            add(JButton(Icons.openedFolder).apply {
                toolTipText = "Open File"
                addActionListener { Commands.open() }
            })
            add(JButton(Icons.picture).apply {
                toolTipText = "Save File As"
                addActionListener { Commands.save() }
            })
            addSeparator()
            add(JButton(Icons.undo).apply {
                toolTipText = "Undo"
                addActionListener { ActionStack.undo() }
            })
            add(JButton(Icons.redo).apply {
                toolTipText = "Redo"
                addActionListener { ActionStack.redo() }
            })
        }, BorderLayout.PAGE_START)

        Components.frame.add(cControl.contentArea)
        val grid = CGrid(cControl)

        val toolbox = DefaultSingleCDockable("toolbox", "Toolbox", JScrollPane(Components.toolbox))
        cControl.addDockable(toolbox)
        toolbox.isVisible = true
        grid.add(0.0, 0.2, 0.2, 1.8, toolbox)

        val tiledView = DefaultSingleCDockable("tiledView", "Tiled View", Components.tiledView)
        cControl.addDockable(tiledView)
        tiledView.isVisible = true

        val animationPreview = DefaultSingleCDockable("animationPreview", "Animation Preview", ComponentFrame(Components.animationPreview))
        cControl.addDockable(animationPreview)
        animationPreview.isVisible = true
        grid.add(0.0, 0.3, 0.6, 1.0, tiledView, animationPreview)

        val layerList = DefaultSingleCDockable("layerList", "Layer List", ComponentFrame(Components.layerList))
        cControl.addDockable(layerList)
        layerList.isVisible = true
        grid.add(0.0, 1.0, 0.6, 1.0, layerList)

        val pixelGrid = DefaultSingleCDockable("pixelGrid", "Pixel Grid", ComponentFrame(PixelGrid))
        cControl.addDockable(pixelGrid)
        pixelGrid.isVisible = true
        grid.add(0.6, 0.3, 0.6, 1.4, pixelGrid)

        val animationTimeline = DefaultSingleCDockable("animationTimeline", "Animation Timeline", ComponentFrame(Components.animationTimeline))
        cControl.addDockable(animationTimeline)
        animationTimeline.isVisible = true
        grid.add(0.6, 1.4, 0.6, 0.6, animationTimeline)

        val miniMap = DefaultSingleCDockable("miniMap", "Mini-Map", ComponentFrame(Components.miniMap))
        cControl.addDockable(miniMap)
        miniMap.isVisible = true
        grid.add(1.0, 0.1, 0.4, 0.3, miniMap)

        val colourPicker = DefaultSingleCDockable("colourPicker", "Colour Picker", JPanel().apply {
            isOpaque = false
            layout = BorderLayout()

            add(Components.colourPicker)

            add(JToolBar().apply {
                add(JCheckBox("Expert Controls").apply {
                    addActionListener {
                        Components.colourPicker.setExpertControlsVisible(this.isSelected)
                    }
                })
            }, BorderLayout.PAGE_END)
        })
        cControl.addDockable(colourPicker)
        colourPicker.isVisible = true
        grid.add(1.0, 0.3, 0.4, 0.6, colourPicker)

        val toolOptions = DefaultSingleCDockable("toolOptions", "Tool Options", JScrollPane(Components.toolOptions))
        cControl.addDockable(toolOptions)
        toolOptions.isVisible = true
        grid.add(1.0, 0.7, 0.4, 0.4, toolOptions)

        val colourShades = DefaultSingleCDockable("colourShades", "Colour Shades", ComponentFrame(Components.colourShades))
        cControl.addDockable(colourShades)
        colourShades.isVisible = true
        grid.add(1.2, 0.3, 0.4, 0.2, colourShades)

        val colourPalette = DefaultSingleCDockable("colourPalette", "Colour Palette", ComponentFrame(Components.colourPalette))
        cControl.addDockable(colourPalette)
        colourPalette.isVisible = true
        grid.add(1.2, 0.5, 0.4, 0.2, colourPalette)

        val colourLibrary = DefaultSingleCDockable("colourLibrary", "Colour Library", ComponentFrame(Components.colourLibrary))
        cControl.addDockable(colourLibrary)
        colourLibrary.isVisible = true
        grid.add(1.2, 0.6, 0.4, 0.2, colourLibrary)

        val actionHistory = DefaultSingleCDockable("actionHistory", "Action History", ComponentFrame(ActionHistory))
        cControl.addDockable(actionHistory)
        actionHistory.isVisible = true
        grid.add(1.6, 0.0, 0.4, 0.4, actionHistory)

        cControl.contentArea.deploy(grid)
    }

    Components.frame.isVisible = true
    Components.frame.setLocationRelativeTo(null)
}
