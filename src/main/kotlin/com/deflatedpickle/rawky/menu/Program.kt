package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.Settings
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem

class Program : JMenu("Program") {
    companion object {
        val window = Settings()
    }

    init {
        add(JMenuItem("Settings", Icons.settings).apply {
            addActionListener {
                with(window) {
                    setLocationRelativeTo(Components.frame)
                    isVisible = true
                }
            }
        })
    }
}