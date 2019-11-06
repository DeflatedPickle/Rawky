package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.dialogue.About
import com.deflatedpickle.rawky.util.Components
import javax.swing.JMenu
import javax.swing.JMenuItem

class Help : JMenu("Help") {
    init {
        add(JMenuItem("About").apply {
            addActionListener {
                with(About()) {
                    setLocationRelativeTo(Components.frame)
                    isVisible = true
                }
            }
        })
    }
}