package com.deflatedpickle.rawky.widget

import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.JLabel
import javax.swing.UIManager

class HyperLabel(text: String, link: URI) : JLabel(text) {
    init {
        foreground = UIManager.getColor("List.selectionBackground")
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

        setText("<html><a href=''>$text</a></html>")
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == 1 && e.clickCount == 2) {
                    Desktop.getDesktop().browse(link)
                }
            }
        })
    }
}