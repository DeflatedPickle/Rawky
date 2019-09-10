package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.utils.Commands
import com.deflatedpickle.rawky.utils.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem

class File : JMenu("File") {
    init {
        add(JMenuItem("New", Icons.create_new).apply { addActionListener { Commands.new() } })
        add(JMenuItem("Open", Icons.opened_folder).apply { addActionListener { Commands.open() } })
        add(JMenuItem("Save As", Icons.picture).apply { addActionListener { Commands.save() } })
    }
}