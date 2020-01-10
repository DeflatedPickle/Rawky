/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.New
import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.EComponent
import com.deflatedpickle.rawky.util.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JSeparator

class File : JMenu("File") {
    init {
        add(JMenuItem("New", Icons.createNew).apply {
            addActionListener {
                with(New()) {
                    setLocationRelativeTo(Components.frame)
                    isVisible = true
                }
            }
        })
        add(JMenuItem("Open", Icons.openedFolder).apply { addActionListener { Commands.open() } })
        add(JMenuItem("Save As", Icons.picture).apply { addActionListener { Commands.save() } })
        add(JSeparator())
        add(JMenu("Import").apply {
            add(JMenuItem("Image").apply { addActionListener { Commands.importImage() } })
            add(JMenu("Colour Palette").apply {
                add(JMenuItem("JASC PAL").apply { addActionListener { Commands.importJascPal() } })
                add(JMenuItem("RexPaint Palette (Colour Library)").apply { addActionListener { Commands.importRexPaintPalette(EComponent.COLOUR_LIBRARY) } })
                add(JMenuItem("RexPaint Palette (Colour Palette)").apply { addActionListener { Commands.importRexPaintPalette(EComponent.COLOUR_PALETTE) } })
            })
            add(JSeparator())
            add(JMenuItem("Dock Layout").apply { addActionListener { Commands.importGridLayout() } })
        })
        add(JMenu("Export").apply {
            add(JMenuItem("Image").apply { addActionListener { Commands.exportImage() } })
            add(JSeparator())
            add(JMenuItem("Dock Layout").apply { addActionListener { Commands.exportGridLayout() } })
        })
    }
}
