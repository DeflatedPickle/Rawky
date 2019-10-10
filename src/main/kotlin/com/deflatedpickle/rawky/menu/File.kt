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
        add(JMenuItem("New", Icons.create_new).apply {
            addActionListener {
                with(New()) {
                    isVisible = true
                    setLocationRelativeTo(Components.frame)
                }
            }
        })
        add(JMenuItem("Open", Icons.opened_folder).apply { addActionListener { Commands.open() } })
        add(JMenuItem("Save As", Icons.picture).apply { addActionListener { Commands.save() } })
        add(JSeparator())
        add(JMenu("Import").apply {
            add(JMenu("Colour Palette").apply {
                add(JMenuItem("JASC PAL").apply { addActionListener { Commands.importJascPal() } })
                add(JMenuItem("RexPaint Palette (Colour Library)").apply { addActionListener { Commands.importRexPaintPallete(EComponent.COLOUR_LIBRARY) } })
                add(JMenuItem("RexPaint Palette (Colour Palette)").apply { addActionListener { Commands.importRexPaintPallete(EComponent.COLOUR_PALETTE) } })
            })
        })
    }
}