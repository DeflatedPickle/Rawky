package com.deflatedpickle.rawky.api.extension

import java.awt.event.ActionEvent
import javax.swing.JMenu
import javax.swing.JMenuItem

fun JMenu.addItem(text: String, action: (ActionEvent) -> Unit) {
    this.add(JMenuItem(text).apply { addActionListener { action(it) } })
}