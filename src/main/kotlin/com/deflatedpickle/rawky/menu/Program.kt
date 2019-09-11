package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.Settings
import com.deflatedpickle.rawky.util.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem

class Program : JMenu("Program") {
    init {
        add(JMenuItem("Settings", Icons.settings).apply { addActionListener { Settings().isVisible = true } })
    }
}