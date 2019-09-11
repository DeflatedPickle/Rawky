package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.New
import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem

class File : JMenu("File") {
    init {
        add(JMenuItem("New", Icons.create_new).apply { addActionListener { New().isVisible = true } })
        add(JMenuItem("Open", Icons.opened_folder).apply { addActionListener { Commands.open() } })
        add(JMenuItem("Save As", Icons.picture).apply { addActionListener { Commands.save() } })
    }
}