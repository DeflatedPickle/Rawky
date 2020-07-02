package com.deflatedpickle.rawky.ui.widget

import java.awt.Color
import java.awt.Font
import javax.swing.JLabel

class ErrorLabel(text: String) : JLabel("<html>$text</html>") {
    init {
        this.font = this.font.deriveFont(Font.BOLD)
        this.foreground = Color.RED
    }
}