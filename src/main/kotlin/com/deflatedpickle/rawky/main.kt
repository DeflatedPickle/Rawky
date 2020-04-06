/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky

import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.api.component.ComponentFrame
import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.component.ColourHistory
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

        addDock("Toolbox", Components.toolbox, 0.0, 0.2, 0.2, 1.8)
        addDock("Tiled View", Components.tiledView, 0.0, 0.3, 0.6, 1.0)
        addDock("Animation Preview", Components.animationPreview, 0.0, 0.3, 0.6, 1.0)
        addDock("Layer List", Components.layerList, 0.0, 1.0, 0.6, 1.0)
        addDock("Pixel Grid", PixelGrid, 0.6, 0.3, 0.6, 1.4)
        addDock("Animation Timeline", Components.animationTimeline, 0.6, 1.4, 0.6, 0.6)
        addDock("Mini-Map", Components.miniMap, 1.0, 0.1, 0.4, 0.3)

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
        Components.grid.add(1.0, 0.3, 0.4, 0.6, colourPicker)

        addDock("Tool Options", Components.toolOptions, 1.0, 0.7, 0.4, 0.4)
        addDock("Colour Shades", Components.colourShades, 1.2, 0.3, 0.4, 0.2)
        addDock("Colour Palette", Components.colourPalette, 1.2, 0.5, 0.4, 0.2)
        addDock("Colour Library", Components.colourLibrary, 1.2, 0.6, 0.4, 0.2)
        addDock("Colour History", ColourHistory, 1.6, 0.0, 0.4, 0.1)
        addDock("Action History", ActionHistory, 1.6, 0.1, 0.4, 0.4)

        cControl.contentArea.deploy(Components.grid)
    }

    Components.frame.isVisible = true
    Components.frame.setLocationRelativeTo(null)
}

fun addDock(title: String, component: Component, x: Double, y: Double, width: Double, height: Double) {
    val dockable = DefaultSingleCDockable(title.toLowerCase().replace(' ', '_'), title, ComponentFrame(component))
    cControl.addDockable(dockable)
    dockable.isVisible = true
    Components.grid.add(x, y, width, height, dockable)
}
