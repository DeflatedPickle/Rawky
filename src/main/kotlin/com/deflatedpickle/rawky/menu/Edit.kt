/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.menu

import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.util.Icons
import java.awt.Event
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class Edit : JMenu("Edit") {
    init {
        add(JMenuItem("Undo", Icons.undo).apply {
            accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK)
            addActionListener { ActionHistory.currentWidget.actionStack.undo() }
        })
        add(JMenuItem("Redo", Icons.redo).apply {
            accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK or Event.SHIFT_MASK)
            addActionListener { ActionHistory.currentWidget.actionStack.redo() }
        })
    }
}
