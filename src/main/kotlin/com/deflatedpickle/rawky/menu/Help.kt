package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.About
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import javax.swing.JMenu
import javax.swing.JMenuItem

class Help : JMenu("Help") {
    init {
        add(JMenuItem("About").apply {
            addActionListener {
                with(About()) {
                    isVisible = true
                    setLocationRelativeTo(Components.frame)
                }
            }
        })
    }
}